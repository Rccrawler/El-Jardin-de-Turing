"""
npc_simulation.py

Versión mejorada: Sistema de vida para NPCs (relaciones, enamoramiento, pareja, reproducción, hijos,
memoria emocional, profesiones básicas y aprendizaje) + gestores de Mundo/Population, guardado/carga
JSON por NPC y optimizaciones ligeras para soportar multitudes.

Diseñado para integrarse con un mundo ASCII. Incluye:
 - Clase NPC (estado, necesidades, memoria, emociones, Q-learning compacto)
 - Population / Mundo que gestionan muchos NPCs usando actualización por lotes (batching)
 - Guardado y carga de NPCs en JSON (tanto por NPC como fichero combinado)
 - Opciones de optimización: actualización por lotes, recorte de memoria, persistencia a intervalos

Uso básico:
  - Instanciar Population(), crear NPCs con population.create_npc(...)
  - Entorno debe implementar las funciones mínimas indicadas en EntornoDummy
  - Llamar population.update_tick(entorno, tick) cada tick del simulador
  - Usar population.save_all(path) / population.load_all(path) para persistencia

"""

import random
import math
import uuid
import json
import os
from collections import deque
from typing import Dict, Any

# ------------------------------
# Utilidades
# ------------------------------

def clamp(v, lo, hi):
    return max(lo, min(hi, v))


def promedio(a, b):
    return (a + b) / 2.0


def generar_nombre():
    return "NPC_" + uuid.uuid4().hex[:6]


def serialize_state_for_json(obj):
    """Convierte estructuras con claves no-serializables (tuplas) a strings."""
    if isinstance(obj, dict):
        new = {}
        for k, v in obj.items():
            if isinstance(k, tuple):
                ks = json.dumps(k)
            else:
                ks = str(k)
            new[ks] = serialize_state_for_json(v)
        return new
    if isinstance(obj, list):
        return [serialize_state_for_json(x) for x in obj]
    if isinstance(obj, deque):
        return [serialize_state_for_json(x) for x in list(obj)]
    return obj


def deserialize_qtable(obj):
    """Intentamos transformar claves de vuelta si parecen tuplas serializadas."""
    new = {}
    for k, v in obj.items():
        # intentar parsear JSON de clave
        try:
            maybe = json.loads(k)
            if isinstance(maybe, list) or isinstance(maybe, tuple):
                key = tuple(maybe)
            else:
                key = k
        except Exception:
            # no era JSON
            key = k
        new[key] = v
    return new

# ------------------------------
# Clase NPC
# ------------------------------
class NPC:
    def __init__(self, nombre=None, x=0, y=0, genero=None, edad=0, genetica=None, padre=None, madre=None):
        self.id = uuid.uuid4().hex
        self.nombre = nombre or generar_nombre()
        self.x = x
        self.y = y
        self.genero = genero if genero in ("M","F") else random.choice(("M","F"))
        self.edad = edad  # ticks

        # Necesidades (0-100)
        self.necesidades = {
            "hambre": random.randint(10, 40),
            "energia": random.randint(60, 100),
            "social": random.randint(20, 60)
        }

        # Emociones (una mezcla simple)
        self.emocion = "neutro"

        # Memoria: recuerdos cortos (deque) y largo (lista)
        # Para optimizacion, corto tiene maxlen controlable
        self.memoria = {
            "corto": deque(maxlen=60),   # recuerdos recientes
            "largo": []                 # recuerdos persistentes
        }

        # Relaciones
        self.relaciones = {
            "pareja_id": None,          # id de la pareja
            "afinidades": {},           # id -> score (-100 a 100)
            "familia_ids": []           # ids de hijos
        }

        # Reproduccion
        self.gestando = False
        self.gestation_ticks_left = 0
        self.padre_id = padre.id if padre else None
        self.madre_id = madre.id if madre else None

        # Genetica y rasgos (0.0 - 1.0)
        if genetica:
            self.genetica = genetica
        else:
            self.genetica = {
                "inteligencia": random.random(),
                "fuerza": random.random(),
                "curiosidad": random.random(),
                "sociabilidad": random.random()
            }

        # Habilidades y profesion
        self.habilidades = {
            "agricultura": 0.0,
            "mineria": 0.0,
            "medicina": 0.0,
            "cocina": 0.0,
            "herreria": 0.0
        }
        self.profesion = None
        self.estudiando = None  # skill name si está estudiando
        self.estudio_ticks = 0

        # Q-learning compacto para decisiones (estado sencillo)
        self.q_table: Dict[Any, Dict[str, float]] = {}
        self.ultima_accion = None
        self.ultimo_estado = None

        # Otros
        self.energia_max = 100
        self.reproduccion_prob = 0.01  # base

    # ------------------------------
    # Percepcion & estado
    # ------------------------------
    def percibir_estado_simple(self, entorno):
        s = (
            self._disc(self.necesidades["hambre"]),
            self._disc(self.necesidades["energia"]),
            self._disc(self.necesidades["social"]),
            self._emocion_categoria()
        )
        vision = entorno.get_casillas_adyacentes(self.x, self.y)
        npcs_cerca = entorno.get_npcs_cercanos(self, rango=1)
        return s, vision, npcs_cerca

    def _disc(self, val):
        if val < 33: return 0
        if val < 66: return 1
        return 2

    def _emocion_categoria(self):
        mapa = {"neutro":0, "feliz":1, "triste":2, "enamorado":3, "enojado":4, "asustado":5}
        return mapa.get(self.emocion, 0)

    # ------------------------------
    # Memoria y recuerdos
    # ------------------------------
    def recordar(self, texto, emocion="neutro", persistente=False):
        rec = {"tick": None, "texto": texto, "emocion": emocion}
        self.memoria["corto"].append(rec)
        if persistente:
            self.memoria["largo"].append(rec)

    def ajustar_afinidad(self, otro_id, delta):
        cur = self.relaciones["afinidades"].get(otro_id, 0)
        cur = clamp(cur + delta, -100, 100)
        self.relaciones["afinidades"][otro_id] = cur

    # ------------------------------
    # Emociones
    # ------------------------------
    def actualizar_emocion(self):
        hambre = self.necesidades["hambre"]
        energia = self.necesidades["energia"]
        social = self.necesidades["social"]

        if hambre > 80 or energia < 15:
            self.emocion = "triste"
        elif self.relaciones["pareja_id"]:
            af = self.relaciones["afinidades"].get(self.relaciones["pareja_id"], 0)
            if af > 40:
                self.emocion = "enamorado"
            elif af > 10:
                self.emocion = "feliz"
            else:
                self.emocion = "neutro"
        else:
            if social > 70 and self.genetica["sociabilidad"] > 0.6:
                self.emocion = "feliz"
            else:
                self.emocion = random.choice(["neutro","feliz","triste"])

    # ------------------------------
    # Conversacion y relacionamiento
    # ------------------------------
    def conversar_con(self, otro):
        temas = ["clima","trabajo","familia","noticias"]
        tema = random.choice(temas)
        frases = {
            "clima": ["Bonito día.", "Hace calor."],
            "trabajo": ["He estado trabajando.", "Aprendí algo nuevo."],
            "familia": ["Mi familia es pequeña.", "Tengo hijos."],
            "noticias": ["Vi algo interesante en el mercado.", "Hoy encontré comida."]
        }
        frase = random.choice(frases[tema])

        base = 1 + int(self.genetica["sociabilidad"] * 3)
        if self.emocion == "feliz":
            delta = base + 1
        elif self.emocion == "enojado":
            delta = -2
        else:
            delta = base

        self.ajustar_afinidad(otro.id, delta)
        otro.ajustar_afinidad(self.id, delta)

        self.recordar(f"Hablé con {otro.nombre}: {frase}", self.emocion)
        otro.recordar(f"Hablé con {self.nombre}: {frase}", otro.emocion)

        af = self.relaciones["afinidades"].get(otro.id, 0)
        if af > 60 and otro.relaciones["pareja_id"] is None and self.relaciones["pareja_id"] is None:
            prob = 0.02 + (self.genetica["curiosidad"] + otro.genetica["curiosidad"]) * 0.02
            if random.random() < prob:
                self.formar_pareja(otro)

        return frase

    def formar_pareja(self, otro):
        self.relaciones["pareja_id"] = otro.id
        otro.relaciones["pareja_id"] = self.id
        self.ajustar_afinidad(otro.id, 30)
        otro.ajustar_afinidad(self.id, 30)
        self.recordar(f"Formé pareja con {otro.nombre}", "feliz", persistente=True)
        otro.recordar(f"Formé pareja con {self.nombre}", "feliz", persistente=True)

    # ------------------------------
    # Reproducción y gestación
    # ------------------------------
    def intentar_reproduccion(self, otro):
        if self.genero == otro.genero:
            return False
        if self.gestando or otro.gestando:
            return False
        if self.edad < 50 or otro.edad < 50:
            return False

        af = (self.relaciones["afinidades"].get(otro.id, 0) + otro.relaciones["afinidades"].get(self.id,0))/2
        base_prob = self.reproduccion_prob + (af/200.0)

        if self.necesidades["energia"] < 30 or otro.necesidades["energia"] < 30:
            base_prob *= 0.2

        if random.random() < base_prob:
            madre = self if self.genero == "F" else otro
            padre = otro if madre is self else self
            madre.gestando = True
            madre.gestation_ticks_left = random.randint(6, 12)
            madre.recordar(f"Quedé embarazada de {padre.nombre}", "feliz", persistente=True)
            padre.recordar(f"Mi pareja {madre.nombre} está embarazada", "feliz", persistente=True)
            return True
        return False

    def tick_gestacion(self, population):
        if not self.gestando:
            return None
        self.gestation_ticks_left -= 1
        if self.gestation_ticks_left <= 0:
            hijo = self.parir(population)
            self.gestando = False
            self.gestation_ticks_left = 0
            return hijo
        return None

    def parir(self, population):
        padre = population.get_npc_by_id(self.relaciones["pareja_id"]) if self.relaciones["pareja_id"] else None
        genetica_hijo = self._mezclar_genetica(padre)
        hijo_nombre = generar_nombre()
        hijo = NPC(nombre=hijo_nombre, x=self.x, y=self.y, genero=random.choice(("M","F")), edad=0, genetica=genetica_hijo, padre=padre, madre=self)
        self.relaciones["familia_ids"].append(hijo.id)
        if padre:
            padre.relaciones["familia_ids"].append(hijo.id)
        self.recordar(f"Nació mi hijo {hijo.nombre}", "feliz", persistente=True)
        if padre:
            padre.recordar(f"Nació mi hijo {hijo.nombre}", "feliz", persistente=True)
        hijo.necesidades["social"] = 30
        population.add_npc(hijo)
        return hijo

    def _mezclar_genetica(self, padre):
        child = {}
        if padre is None:
            for k,v in self.genetica.items():
                child[k] = clamp(v * 0.9 + random.random()*0.2, 0.0, 1.0)
        else:
            for k in self.genetica.keys():
                p_val = padre.genetica.get(k, random.random())
                m_val = self.genetica.get(k, random.random())
                val = promedio(p_val, m_val) + (random.random()-0.5)*0.1
                child[k] = clamp(val, 0.0, 1.0)
        return child

    # ------------------------------
    # Profesiones y aprendizaje
    # ------------------------------
    def empezar_estudio(self, skill_name, duracion_ticks=50):
        if skill_name not in self.habilidades:
            return False
        self.estudiando = skill_name
        self.estudio_ticks = duracion_ticks
        self.recordar(f"Empecé a estudiar {skill_name}")
        return True

    def tick_estudio(self):
        if not self.estudiando:
            return
        self.estudio_ticks -= 1
        progreso = 0.01 + 0.02 * self.genetica["inteligencia"]
        self.habilidades[self.estudiando] += progreso
        if self.habilidades[self.estudio_ticks] > 1.0 if False else False:
            pass
        if self.habilidades[self.estudiando] > 1.0:
            self.habilidades[self.estudiando] = 1.0
        if self.estudio_ticks <= 0:
            self.recordar(f"Terminé de estudiar {self.estudiando}", "feliz", persistente=True)
            if self.habilidades[self.estudiando] > 0.3 and self.profesion is None:
                self.profesion = self.estudiando
            self.estudiando = None
            self.estudio_ticks = 0

    # ------------------------------
    # Toma decisiones y actua
    # ------------------------------
    def elegir_accion(self, estado, acciones_posibles, epsilon=0.2):
        if random.random() < epsilon:
            return random.choice(acciones_posibles)
        self.q_table.setdefault(estado, {})
        for a in acciones_posibles:
            self.q_table[estado].setdefault(a, 0.0)
        return max(self.q_table[estado], key=self.q_table[estado].get)

    def aprender_q(self, estado, accion, recompensa, nuevo_estado):
        alpha = 0.25
        gamma = 0.8
        self.q_table.setdefault(estado, {})
        self.q_table[estado].setdefault(accion, 0.0)
        self.q_table.setdefault(nuevo_estado, {})
        max_q_nuevo = max(self.q_table[nuevo_estado].values(), default=0)
        viejo = self.q_table[estado][accion]
        self.q_table[estado][accion] = viejo + alpha * (recompensa + gamma * max_q_nuevo - viejo)

    def actuar(self, accion, entorno):
        if accion == "mover_norte":
            self.y -= 1
        elif accion == "mover_sur":
            self.y += 1
        elif accion == "mover_este":
            self.x += 1
        elif accion == "mover_oeste":
            self.x -= 1
        elif accion == "comer":
            comida = entorno.recolectar_comida_en(self.x, self.y)
            if comida:
                self.necesidades["hambre"] = clamp(self.necesidades["hambre"] - 40, 0, 100)
                self.recordar("Comí comida.", "feliz")
        elif accion == "dormir":
            self.necesidades["energia"] = clamp(self.necesidades["energia"] + 50, 0, 100)
            self.recordar("Dormí y descansé.", "feliz")
        elif accion == "hablar":
            npcs = entorno.get_npcs_cercanos(self, rango=1)
            if npcs:
                otros = [n for n in npcs if n.id != self.id]
                if otros:
                    otro = random.choice(otros)
                    self.conversar_con(otro)
        elif accion == "estudiar":
            if not self.estudiando:
                preferencia = sorted(self.habilidades.items(), key=lambda kv: kv[1])
                skill = preferencia[0][0]
                self.empezar_estudio(skill, duracion_ticks=80)
        elif accion == "trabajar":
            if self.profesion:
                self.habilidades[self.profesion] += 0.002
                self.necesidades["hambre"] += 5
                self.necesidades["energia"] -= 3
                self.recordar(f"Trabajé como {self.profesion}.")

        # deterioro natural
        self.necesidades["hambre"] = clamp(self.necesidades["hambre"] + 1, 0, 100)
        self.necesidades["energia"] = clamp(self.necesidades["energia"] - 0.5, 0, 100)
        self.necesidades["social"] = clamp(self.necesidades["social"] + 0.2, 0, 100)

        self.recordar(f"Acción: {accion}")
        self.actualizar_emocion()

    # ------------------------------
    # Persistencia (JSON)
    # ------------------------------
    def to_dict(self):
        data = {
            "id": self.id,
            "nombre": self.nombre,
            "x": self.x,
            "y": self.y,
            "genero": self.genero,
            "edad": self.edad,
            "necesidades": self.necesidades,
            "emocion": self.emocion,
            "memoria": {
                "corto": list(self.memoria["corto"]),
                "largo": self.memoria["largo"]
            },
            "relaciones": self.relaciones,
            "gestando": self.gestando,
            "gestation_ticks_left": self.gestation_ticks_left,
            "padre_id": self.padre_id,
            "madre_id": self.madre_id,
            "genetica": self.genetica,
            "habilidades": self.habilidades,
            "profesion": self.profesion,
            "estudiando": self.estudiando,
            "estudio_ticks": self.estudio_ticks,
            "q_table": serialize_state_for_json(self.q_table)
        }
        return data

    @classmethod
    def from_dict(cls, data):
        # crear instancia mínima y asignar campos
        npc = cls(nombre=data.get("nombre"), x=data.get("x",0), y=data.get("y",0), genero=data.get("genero"), edad=data.get("edad",0), genetica=data.get("genetica"))
        npc.id = data.get("id", npc.id)
        npc.necesidades = data.get("necesidades", npc.necesidades)
        npc.emocion = data.get("emocion", npc.emocion)
        # memoria corto como deque
        corto = data.get("memoria", {}).get("corto", [])
        npc.memoria["corto"] = deque(corto, maxlen=60)
        npc.memoria["largo"] = data.get("memoria", {}).get("largo", [])
        npc.relaciones = data.get("relaciones", npc.relaciones)
        npc.gestando = data.get("gestando", False)
        npc.gestation_ticks_left = data.get("gestation_ticks_left", 0)
        npc.padre_id = data.get("padre_id")
        npc.madre_id = data.get("madre_id")
        npc.habilidades = data.get("habilidades", npc.habilidades)
        npc.profesion = data.get("profesion")
        npc.estudiando = data.get("estudiando")
        npc.estudio_ticks = data.get("estudio_ticks", 0)
        q_raw = data.get("q_table", {})
        npc.q_table = deserialize_qtable(q_raw)
        return npc

    # ------------------------------
    # Ciclo principal
    # ------------------------------
    def update(self, entorno, population):
        estado, vision, npcs_cerca = self.percibir_estado_simple(entorno)
        acciones = ["mover_norte","mover_sur","mover_este","mover_oeste","comer","dormir","hablar","estudiar","trabajar"]
        accion = self.elegir_accion(estado, acciones)
        self.actuar(accion, entorno)
        self.tick_estudio()
        for otro in npcs_cerca:
            if otro.id == self.id:
                continue
            if random.random() < 0.1:
                self.conversar_con(otro)
            if random.random() < 0.02:
                self.intentar_reproduccion(otro)
        hijo = self.tick_gestacion(population)
        recompensa = 0
        recompensa -= self.necesidades["hambre"] * 0.05
        recompensa += (self.energia_max - abs(50 - self.necesidades["energia"])) * 0.01
        if self.emocion in ("feliz","enamorado"):
            recompensa += 1
        nuevo_estado, _, _ = self.percibir_estado_simple(entorno)
        if self.ultima_accion is not None:
            self.aprender_q(self.ultimo_estado, self.ultima_accion, recompensa, nuevo_estado)
        self.ultimo_estado = estado
        self.ultima_accion = accion
        self.edad += 1

# ------------------------------
# Population / Mundo manager con batching y persistencia
# ------------------------------
class Population:
    def __init__(self, batch_fraction=0.25, persist_interval_ticks=200, memory_limit=60):
        """
        batch_fraction: qué fracción de NPCs se actualizan cada tick (por defecto 25%).
                        Si p=1.0 actualiza todos. Valores pequeños reducen carga por tick.
        persist_interval_ticks: cada cuántos ticks se guarda automáticamente
        memory_limit: máximo recuerdos en memoria corta
        """
        self.npcs: Dict[str, NPC] = {}
        self._update_index = 0
        self.tick_counter = 0
        self.batch_fraction = clamp(batch_fraction, 0.05, 1.0)
        self.persist_interval_ticks = persist_interval_ticks
        self.memory_limit = memory_limit

    def add_npc(self, npc: NPC):
        # ajustar memoria corto maxlen según configuración
        npc.memoria["corto"] = deque(npc.memoria.get("corto", []), maxlen=self.memory_limit)
        self.npcs[npc.id] = npc

    def remove_npc(self, npc: NPC):
        if npc.id in self.npcs:
            del self.npcs[npc.id]

    def create_npc(self, nombre=None, x=0, y=0, genero=None, edad=0, genetica=None):
        n = NPC(nombre=nombre, x=x, y=y, genero=genero, edad=edad, genetica=genetica)
        self.add_npc(n)
        return n

    def get_npc_by_id(self, id_):
        return self.npcs.get(id_)

    def all_npcs(self):
        return list(self.npcs.values())

    def save_all(self, folder_path="npc_save", single_file=False):
        os.makedirs(folder_path, exist_ok=True)
        if single_file:
            data = {nid: npc.to_dict() for nid, npc in self.npcs.items()}
            with open(os.path.join(folder_path, "population.json"), "w", encoding="utf-8") as f:
                json.dump(data, f, indent=2, ensure_ascii=False)
            return
        # por-npc
        for nid, npc in self.npcs.items():
            path = os.path.join(folder_path, f"{nid}.json")
            with open(path, "w", encoding="utf-8") as f:
                json.dump(npc.to_dict(), f, indent=2, ensure_ascii=False)

    def load_all(self, folder_path="npc_save", single_file=False):
        if single_file:
            path = os.path.join(folder_path, "population.json")
            if not os.path.exists(path):
                return
            with open(path, "r", encoding="utf-8") as f:
                data = json.load(f)
            for nid, d in data.items():
                npc = NPC.from_dict(d)
                self.add_npc(npc)
            return
        if not os.path.exists(folder_path):
            return
        for fname in os.listdir(folder_path):
            if not fname.endswith('.json'):
                continue
            path = os.path.join(folder_path, fname)
            with open(path, "r", encoding="utf-8") as f:
                d = json.load(f)
            npc = NPC.from_dict(d)
            self.add_npc(npc)

    def update_tick(self, entorno, tick: int):
        """
        Actualiza sólo un subconjunto de NPCs cada tick (batching) para distribuir carga.
        Si batch_fraction==1.0 se actualizan todos.
        """
        n = len(self.npcs)
        if n == 0:
            return
        batch_size = max(1, int(math.ceil(n * self.batch_fraction)))

        ids = list(self.npcs.keys())
        # calcular slice
        start = self._update_index
        end = start + batch_size
        slice_ids = ids[start:end]
        # wrap-around
        if end > n:
            slice_ids = ids[start:n] + ids[0:(end % n)]
        for nid in slice_ids:
            npc = self.npcs.get(nid)
            if npc:
                npc.update(entorno, self)
        # avanzar índice
        self._update_index = (self._update_index + batch_size) % n

        # tick bookkeeping
        self.tick_counter += 1
        if self.tick_counter % self.persist_interval_ticks == 0:
            # guardado automático ligero (no bloqueante en este ejemplo)
            self.save_all()

# ------------------------------
# Dummy entorno de ejemplo (para pruebas)
# ------------------------------
class EntornoDummy:
    def __init__(self, width=20, height=10):
        self.width = width
        self.height = height
        self.map = [["." for _ in range(width)] for __ in range(height)]
        self.population: Population = None

    def set_population(self, pop: Population):
        self.population = pop

    def get_casillas_adyacentes(self, x, y):
        vals = []
        for dx in (-1,0,1):
            for dy in (-1,0,1):
                nx, ny = x+dx, y+dy
                if 0 <= nx < self.width and 0 <= ny < self.height:
                    vals.append(self.map[ny][nx])
        return vals

    def get_npcs_cercanos(self, npc, rango=1):
        res = []
        if not self.population:
            return res
        for other in self.population.all_npcs():
            if other.id == npc.id:
                continue
            if abs(other.x - npc.x) <= rango and abs(other.y - npc.y) <= rango:
                res.append(other)
        return res

    def recolectar_comida_en(self, x, y):
        if random.random() < 0.08:
            return True
        return False

# ------------------------------
# Ejemplo de uso
# ------------------------------
if __name__ == "__main__":
    pop = Population(batch_fraction=0.35, persist_interval_ticks=150, memory_limit=80)
    ent = EntornoDummy(40, 20)
    ent.set_population(pop)

    # crear poblacion inicial
    for i in range(80):
        n = pop.create_npc(x=random.randint(0,39), y=random.randint(0,19))

    # simular ticks
    for tick in range(2000):
        pop.update_tick(ent, tick)
        if tick % 100 == 0:
            print(f"Tick {tick}, poblacion: {len(pop.npcs)}")

    # guardar al final
    pop.save_all(folder_path="npc_save_final")
    print("Simulación terminada, guardado en 'npc_save_final' .")
