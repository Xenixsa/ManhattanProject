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

# Load data
xs = []
ys = []
ts = []
with open(csv_path, newline="") as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        xs.append(int(row["collision_x"]))
        ys.append(int(row["collision_y"]))
        ts.append(float(row["time_seconds"]))

if not xs:
    raise SystemExit(f"No collision data found in {csv_path}.")

# Parameters (tweakable)
x_bins = 96
y_bins = 54
time_bins = 80

# Compute time bins
t_min = min(ts)
t_max = max(ts)
if t_min == t_max:
    t_max = t_min + 1.0

t_edges = np.linspace(t_min, t_max, time_bins + 1)

# Build 3D histogram: time x x_bins x y_bins
hist_time = np.zeros((time_bins, x_bins, y_bins), dtype=float)

x_edges = np.linspace(0, 1920, x_bins + 1)
y_edges = np.linspace(0, 1080, y_bins + 1)

# Assign events to bins
for x, y, t in zip(xs, ys, ts):
    tx = np.searchsorted(t_edges, t, side='right') - 1
    if tx < 0 or tx >= time_bins:
        continue
    xi = np.searchsorted(x_edges, x, side='right') - 1
    yi = np.searchsorted(y_edges, y, side='right') - 1
    if 0 <= xi < x_bins and 0 <= yi < y_bins:
        hist_time[tx, xi, yi] += 1

# Compute per-bin mean inter-arrival rate (collisions per second)
# For each spatial bin, collect times
rate_map = np.zeros((x_bins, y_bins), dtype=float)
from collections import defaultdict
bin_times = defaultdict(list)
for x, y, t in zip(xs, ys, ts):
    xi = int(np.searchsorted(x_edges, x, side='right') - 1)
    yi = int(np.searchsorted(y_edges, y, side='right') - 1)
    if 0 <= xi < x_bins and 0 <= yi < y_bins:
        bin_times[(xi, yi)].append(t)

for (xi, yi), times_list in bin_times.items():
    if len(times_list) >= 2:
        times_sorted = np.sort(times_list)
        diffs = np.diff(times_sorted)
        mean_diff = np.mean(diffs)
        if mean_diff > 0:
            rate_map[xi, yi] = 1.0 / mean_diff

# Save rate map image
rate_out_dir = os.path.join(base_dir, 'app', 'simulation_stats')
if not os.path.isdir(rate_out_dir):
    os.makedirs(rate_out_dir, exist_ok=True)

plt.figure(figsize=(10, 6))
plt.imshow(rate_map.T, origin='lower', extent=[0, 1920, 0, 1080], cmap='inferno', aspect='auto')
plt.colorbar(label='Collision rate (1/s)')
plt.xlabel('X position')
plt.ylabel('Y position')
plt.title('Per-bin collision rate (1 / mean inter-arrival)')
rate_path = os.path.join(rate_out_dir, 'collision_rate_map.png')
plt.savefig(rate_path, dpi=150)
print(f"Saved collision rate map to {rate_path}")

try:
    from PIL import Image
    img = Image.open(rate_path)
    img.save(os.path.join(rate_out_dir, 'collision_rate_map.gif'), format='GIF')
    print(f"Saved collision rate map GIF to {os.path.join(rate_out_dir, 'collision_rate_map.gif')}")
except Exception:
    pass

plt.show()

# Create animation of time-sliced heatmaps
fig, ax = plt.subplots(figsize=(10, 6))
extent = [0, 1920, 0, 1080]

# Normalize color scale across all time slices
vmin = 0
vmax = hist_time.max() if hist_time.max() > 0 else 1
im = ax.imshow(hist_time[0].T, origin='lower', extent=extent, cmap='hot', vmin=vmin, vmax=vmax, aspect='auto')
ax.set_xlabel('X position')
ax.set_ylabel('Y position')
cb = plt.colorbar(im, ax=ax, label='Collision count')

def update(frame):
    im.set_data(hist_time[frame].T)
    ax.set_title(f'Time slice {frame+1}/{time_bins}  t~{(t_edges[frame]+t_edges[frame+1])/2:.2f}s')
    return [im]

anim = animation.FuncAnimation(fig, update, frames=time_bins, blit=False)

# Animation saving removed (no GIFs). Show interactively instead.
plt.show()
