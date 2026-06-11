import csv
import glob
import os
import matplotlib.pyplot as plt

base_dir = os.path.dirname(os.path.abspath(__file__))

possible_patterns = [
    os.path.join(base_dir, "app", "simulation_stats", "simulation_stats_*.csv"),
    os.path.join(base_dir, "app", "simulation_stats", "simulation_stats.csv"),
    os.path.join(base_dir, "app", "simulation_stats.csv"),
    os.path.join(base_dir, "simulation_stats.csv"),
]

def find_latest_file(patterns):
    candidates = []
    for pattern in patterns:
        candidates.extend(glob.glob(pattern))
    if not candidates:
        return None
    return max(candidates, key=os.path.getmtime)

csv_path = find_latest_file(possible_patterns)
if csv_path is None:
    raise SystemExit(f"Could not find simulation_stats.csv. Checked: {', '.join(possible_patterns)}")

times, neutrons, atoms, fragments, fps_vals = [], [], [], [], []

with open(csv_path, newline="") as f:
    reader = csv.DictReader(f)
    for row in reader:
        times.append(float(row["time_seconds"]))
        neutrons.append(int(row["active_neutrons"]))
        atoms.append(int(row["visible_atoms"]))
        fragments.append(int(row["active_fragments"]))
        fps_vals.append(float(row.get("fps", 0)))

if not times:
    raise SystemExit(f"No data in {csv_path}.")

fig, axes = plt.subplots(4, 1, figsize=(12, 14), sharex=True)
fig.suptitle("Simulation Stats", fontsize=14)

axes[0].plot(times, neutrons, color="tab:blue", linewidth=1.5)
axes[0].set_ylabel("Neutrony")
axes[0].grid(True, linestyle="--", alpha=0.4)

axes[1].plot(times, atoms, color="tab:orange", linewidth=1.5)
axes[1].set_ylabel("Atomy")
axes[1].grid(True, linestyle="--", alpha=0.4)

axes[2].plot(times, fragments, color="tab:green", linewidth=1.5)
axes[2].set_ylabel("Fragmenty")
axes[2].grid(True, linestyle="--", alpha=0.4)

axes[3].plot(times, fps_vals, color="tab:red", linewidth=1.0, alpha=0.8)
axes[3].set_ylabel("FPS")
axes[3].set_xlabel("Czas (s)")
axes[3].grid(True, linestyle="--", alpha=0.4)

plt.tight_layout()

out_dir = os.path.join(base_dir, "app", "simulation_stats")
os.makedirs(out_dir, exist_ok=True)
out_path = os.path.join(out_dir, "simulation_counts.png")
plt.savefig(out_path, dpi=150)
print(f"Saved to {out_path}")
plt.show()