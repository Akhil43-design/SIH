# Multi-Vehicle Quantum-Inspired Fleet Route Optimizer
## Algorithm Architecture & Documentation (Problem Statement 137)

---

### 1. Problem Formulation
Problem Statement 137 addresses the **Multi-Vehicle Capacitated Vehicle Routing Problem with Time Windows and Traffic Conditions (CVRPTW-T)**.
Given:
- A depot $W$ (or multiple depots)
- A fleet of $V$ heterogeneous vehicles $\{V_1, V_2, \dots, V_V\}$ with capacity limits $Q_v$, fuel rates, and operating cost structures
- A set of $N$ customer locations $\{C_1, C_2, \dots, C_N\}$, each with demand $q_i$, service time $s_i$, priority level $p_i$, and delivery time window $[e_i, l_i]$
- A road network graph with traffic congestion factors $\tau_{ij}$

**Objective:** Jointly determine vehicle-to-customer assignments and route sequence for each vehicle to minimize a multi-attribute objective (distance, travel time, fuel consumption, operating cost) while satisfying all capacity and time-window constraints.

---

### 2. Fleet Representation & Multi-Vehicle Chromosome
To simultaneously optimize customer sequencing and vehicle partitioning, the quantum chromosome consists of:
1. **Position Q-Bit Register:** $N$ position Q-bits ($N \times N$ matrix) modeling the probability distribution of customer visit permutations $\pi = (c_1, c_2, \dots, c_N)$.
2. **Vehicle Assignment Q-Bit Register:** $N$ vehicle assignment Q-bits ($N \times V$ matrix), where $p_{i,v}$ represents the probability that customer $i$ is served by vehicle $v \in \{1, \dots, V\}$.

---

### 3. Quantum Route Generation & Assignment
1. **Quantum Permutation Measurement:** Measures position Q-bits with $\epsilon$-greedy exploration to yield an ordered sequence of all customers.
2. **Quantum Vehicle Measurement:** For each customer in the sequence, measures assignment Q-bits to assign the customer to a specific vehicle $v$.
3. **Route Construction:** For each vehicle $v$, assembles the route $\text{Depot} \to c_{v,1} \to c_{v,2} \to \dots \to c_{v,k} \to \text{Depot}$.
4. **Permutation Integrity:** Guarantees that every customer appears exactly once across the fleet (zero duplicates, zero unassigned customers).

---

### 4. Constraints & Multi-Objective Fitness Evaluation
- **Capacity Constraint:** $\sum_{c \in R_v} q_c \le Q_v$. Overload is strictly penalized.
- **Time Windows & Lateness:** Tracks arrival time $t_{\text{arr}}$, waiting time $t_{\text{wait}} = \max(0, e_i - t_{\text{arr}})$, and lateness $t_{\text{late}} = \max(0, t_{\text{arr}} - l_i)$.
- **Priority Scaling:** Lateness penalty is weighted by priority multiplier (HIGH: 3.0x, MEDIUM: 2.0x, LOW: 1.0x).
- **Traffic Multiplier:** Travel time adjusted dynamically based on `TrafficCondition` (LOW: 1.0x, MEDIUM: 1.25x, HIGH: 1.60x).
- **Fitness Function:**
$$\text{Fitness} = w_d \frac{D}{D_{\max}} + w_t \frac{T}{T_{\max}} + w_f \frac{F}{F_{\max}} + w_c \frac{C}{C_{\max}} + \text{Penalties}_{\text{cap}} + \text{Penalties}_{\text{time}}$$

---

### 5. Multi-Vehicle Local Intensification Heuristic
`MultiVehicleLocalImprover` applies four operators:
1. **Intra-Route 2-Opt:** Reverses customer subsegments within a vehicle route to untangle road crossings.
2. **Intra-Route Swap:** Transposes two customer positions within a vehicle route.
3. **Inter-Route Relocate (Insertion):** Moves a customer from Vehicle $V_1$ to an optimal insertion index in Vehicle $V_2$.
4. **Inter-Route Swap:** Swaps customer $C_1 \in V_1$ with customer $C_2 \in V_2$.

Moves are strictly accepted if they improve the overall fleet fitness.

---

### 6. Quantum Evolution Loop
1. Initialize Quantum Fleet Population in equal superposition ($1/N$ for positions, $1/V$ for vehicles).
2. Measure quantum states to generate candidate fleet plans.
3. Apply multi-vehicle local improvement to candidate plans.
4. Update `globalBestPlan`.
5. Shift quantum probability distributions toward the global best customer permutation and vehicle assignments using learning rate $\alpha = 0.05$.
6. Adapt exploration rate with stagnation detection and escape boosts.

---

### 7. Dynamic Customer Foundation
`FleetCustomerManager` allows dynamic customer additions, deletions, and priority updates, triggering instant re-optimization of fleet routes.

---

### 8. Example Fleet Solution (10 Customers, 3 Vehicles)
```
Vehicle 1 (Cap: 90.0, Demand: 70.0):
  Depot -> A [D:25, P:HIGH] -> E [D:35, P:HIGH] -> F [D:10, P:MEDIUM] -> Depot

Vehicle 2 (Cap: 80.0, Demand: 65.0):
  Depot -> B [D:15, P:MEDIUM] -> I [D:15, P:HIGH] -> J [D:30, P:MEDIUM] -> Depot

Vehicle 3 (Cap: 100.0, Demand: 95.0):
  Depot -> C [D:30, P:HIGH] -> H [D:25, P:MEDIUM] -> D [D:20, P:LOW] -> G [D:20, P:LOW] -> Depot
```
All constraints satisfied: Capacity violations = 0, Time violations = 0, Unassigned = 0, Duplicates = 0.
