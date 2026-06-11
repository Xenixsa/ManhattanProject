import csv
import os
import matplotlib.pyplot as plt

base_dir = os.path.dirname(os.path.abspath(__file__))
possible_paths = [
    os.path.join(base_dir, "app", "simulation_stats", "simulation_stats.csv"),
    os.path.join(base_dir, "app", "simulation_stats.csv"),
    os.path.join(base_dir, "simulation_stats.csv"),
]

csv_path = next((path for path in possible_paths if os.path.isfile(path)), None)
if csv_path is None:
    raise SystemExit(
        "Could not find simulation_stats.csv. Run the simulation first to generate it. "
        f"Checked: {', '.join(possible_paths)}"
    )

times = []
neutron_counts = []
atom_counts = []
fragment_counts = []

with open(csv_path, newline="") as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        times.append(float(row["time_seconds"]))
        neutron_counts.append(int(row["active_neutrons"]))
        atom_counts.append(int(row["visible_atoms"]))
        fragment_counts.append(int(row["active_fragments"]))

if not times:
    raise SystemExit(f"No data found in {csv_path}. Run the simulation first to generate the CSV.")

plt.figure(figsize=(12, 6))
plt.plot(times, neutron_counts, label="Neutrony", color="tab:blue", linewidth=2)
plt.plot(times, atom_counts, label="Atomy", color="tab:orange", linewidth=2)
plt.plot(times, fragment_counts, label="Fragmenty", color="tab:green", linewidth=2)

plt.title("Simulation Particle Counts Over Time")
plt.xlabel("Time (seconds)")
plt.ylabel("Count")
plt.grid(True, linestyle="--", alpha=0.4)
plt.legend()
plt.tight_layout()
plt.show()
