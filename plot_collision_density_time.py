import csv
import glob
import os
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation

base_dir = os.path.dirname(os.path.abspath(__file__))
possible_patterns = [
    os.path.join(base_dir, "app", "simulation_stats", "collision_positions_*.csv"),
    os.path.join(base_dir, "app", "simulation_stats", "collision_positions.csv"),
    os.path.join(base_dir, "app", "collision_positions.csv"),
    os.path.join(base_dir, "collision_positions.csv"),
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
    raise SystemExit(
        "Could not find collision_positions.csv. Run the simulation first to generate it. "
        f"Checked: {', '.join(possible_patterns)}"
    )

xs, ys, ts = [], [], []
with open(csv_path, newline="") as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        xs.append(int(row["collision_x"]))
        ys.append(int(row["collision_y"]))
        ts.append(float(row["time_seconds"]))

if not xs:
    raise SystemExit(f"No collision data found in {csv_path}.")

x_bins = 96
y_bins = 54
time_bins = 80

t_min, t_max = min(ts), max(ts)
if t_min == t_max:
    t_max = t_min + 1.0

t_edges = np.linspace(t_min, t_max, time_bins + 1)
x_edges = np.linspace(0, 1920, x_bins + 1)
y_edges = np.linspace(0, 1080, y_bins + 1)

hist_time = np.zeros((time_bins, x_bins, y_bins), dtype=float)
for x, y, t in zip(xs, ys, ts):
    tx = np.searchsorted(t_edges, t, side='right') - 1
    if tx < 0 or tx >= time_bins:
        continue
    xi = np.searchsorted(x_edges, x, side='right') - 1
    yi = np.searchsorted(y_edges, y, side='right') - 1
    if 0 <= xi < x_bins and 0 <= yi < y_bins:
        hist_time[tx, xi, yi] += 1

# Collision density + FPS window
stats_patterns = [
    os.path.join(base_dir, "app", "simulation_stats", "simulation_stats_*.csv"),
    os.path.join(base_dir, "app", "simulation_stats", "simulation_stats.csv"),
    os.path.join(base_dir, "app", "simulation_stats.csv"),
    os.path.join(base_dir, "simulation_stats.csv"),
]
stats_path = find_latest_file(stats_patterns)
fps_times, fps_vals = [], []
if stats_path:
    with open(stats_path, newline="") as f:
        reader = csv.DictReader(f)
        for row in reader:
            if "fps" in row:
                fps_times.append(float(row["time_seconds"]))
                fps_vals.append(float(row["fps"]))

bin_centers = (t_edges[:-1] + t_edges[1:]) / 2
collisions_per_bin = [hist_time[i].sum() for i in range(time_bins)]

fig1, (ax_density, ax_fps) = plt.subplots(2, 1, figsize=(12, 8), sharex=False)
fig1.suptitle("Kolizje i FPS", fontsize=13)

ax_density.plot(bin_centers, collisions_per_bin, color="tab:purple", linewidth=1.5)
ax_density.set_ylabel("Kolizje / przedział czasu")
ax_density.set_xlabel("Czas (s)")
ax_density.grid(True, linestyle="--", alpha=0.4)
ax_density.set_title("Gęstość kolizji w czasie")

if fps_times:
    ax_fps.plot(fps_times, fps_vals, color="tab:red", linewidth=1.0, alpha=0.8)
ax_fps.set_ylabel("FPS")
ax_fps.set_xlabel("Czas (s)")
ax_fps.grid(True, linestyle="--", alpha=0.4)
ax_fps.set_title("FPS w czasie")

plt.tight_layout()

rate_out_dir = os.path.join(base_dir, 'app', 'simulation_stats')
os.makedirs(rate_out_dir, exist_ok=True)
density_out = os.path.join(rate_out_dir, "collision_density_fps.png")
plt.savefig(density_out, dpi=150)
print(f"Saved density+fps plot to {density_out}")

# Animated heatmap window
fig2, ax2 = plt.subplots(figsize=(10, 6))
extent = [0, 1920, 0, 1080]
vmax = hist_time.max() if hist_time.max() > 0 else 1
im = ax2.imshow(hist_time[0].T, origin='upper', extent=extent, cmap='hot', vmin=0, vmax=vmax, aspect='auto')
ax2.set_xlabel('X position')
ax2.set_ylabel('Y position')
plt.colorbar(im, ax=ax2, label='Collision count')

def update(frame):
    im.set_data(hist_time[frame].T)
    ax2.set_title(f'Time slice {frame+1}/{time_bins}  t~{(t_edges[frame]+t_edges[frame+1])/2:.2f}s')
    return [im]

anim = animation.FuncAnimation(fig2, update, frames=time_bins, blit=False)

plt.show()