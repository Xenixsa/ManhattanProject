import csv
import glob
import os
import numpy as np
import matplotlib.pyplot as plt

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

xs = []
ys = []

with open(csv_path, newline="") as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        xs.append(int(row["collision_x"]))
        ys.append(int(row["collision_y"]))

if not xs:
    raise SystemExit(f"No collision data found in {csv_path}.")

# Higher-resolution bins to give finer regional detail
# (still aggregated compared to raw pixels)
x_bins = 192
y_bins = 108
heatmap, xedges, yedges = np.histogram2d(xs, ys, bins=[x_bins, y_bins], range=[[0, 1920], [0, 1080]])

# Smooth a bit. Prefer SciPy if available, otherwise use a small NumPy convolution.
try:
    from scipy.ndimage import gaussian_filter
    heatmap_smoothed = gaussian_filter(heatmap.T, sigma=1.0)
except Exception:
    # small Gaussian-like kernel via outer product
    kernel_1d = np.array([0.25, 0.5, 0.25])
    kernel = np.outer(kernel_1d, kernel_1d)
    padded = np.pad(heatmap.T, pad_width=1, mode='constant', constant_values=0)
    heatmap_smoothed = np.zeros_like(heatmap.T, dtype=float)
    for i in range(heatmap_smoothed.shape[0]):
        for j in range(heatmap_smoothed.shape[1]):
            heatmap_smoothed[i, j] = np.sum(padded[i:i+3, j:j+3] * kernel)

fig, ax = plt.subplots(figsize=(10, 6))
# extent to map bin centers to image coordinates
extent = [xedges[0], xedges[-1], yedges[0], yedges[-1]]
im = ax.imshow(heatmap_smoothed, origin='upper', extent=extent, cmap='hot', aspect='auto')
ax.set_xlabel('X position')
ax.set_ylabel('Y position')
plt.colorbar(im, ax=ax, label='Collision count')
plt.tight_layout()
# save to file for easy comparison
out_dir = os.path.join(base_dir, 'app', 'simulation_stats')
if not os.path.isdir(out_dir):
    os.makedirs(out_dir, exist_ok=True)
out_path = os.path.join(out_dir, 'collision_heatmap_2d.png')
plt.savefig(out_path, dpi=150)
print(f"Saved 2D heatmap to {out_path}")

plt.show()
