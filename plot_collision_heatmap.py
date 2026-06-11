import csv
import glob
import os
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

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
zs = []

time_values = []

with open(csv_path, newline="") as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        x = int(row["collision_x"])
        y = int(row["collision_y"])
        t = float(row["time_seconds"])
        xs.append(x)
        ys.append(y)
        time_values.append(t)

if not xs:
    raise SystemExit(f"No collision data found in {csv_path}. Run the simulation first to generate it.")

def smooth_array(array, kernel_size=3):
    kernel = np.ones((kernel_size, kernel_size), dtype=float)
    kernel /= kernel.sum()
    padded = np.pad(array, pad_width=kernel_size // 2, mode='constant', constant_values=0)
    smoothed = np.zeros_like(array, dtype=float)
    for i in range(array.shape[0]):
        for j in range(array.shape[1]):
            window = padded[i:i + kernel_size, j:j + kernel_size]
            smoothed[i, j] = np.sum(window * kernel)
    return smoothed

# Create a coarse 2D histogram to show broader collision regions instead of exact pixel outlines.
x_bins = 48
y_bins = 27
heatmap, xedges, yedges = np.histogram2d(xs, ys, bins=[x_bins, y_bins], range=[[0, 1920], [0, 1080]])
heatmap = smooth_array(heatmap, kernel_size=3)

x_centers = (xedges[:-1] + xedges[1:]) / 2
y_centers = (yedges[:-1] + yedges[1:]) / 2
X, Y = np.meshgrid(x_centers, y_centers)

fig = plt.figure(figsize=(14, 8))
ax = fig.add_subplot(111, projection='3d')

# Histogram2d returns array shaped (x_bins, y_bins), so transpose for plotting
ax.plot_surface(X, Y, heatmap.T, cmap='hot', edgecolor='none', antialiased=True)

ax.set_xlabel('X position')
ax.set_ylabel('Y position')
ax.set_zlabel('Collision count')
ax.set_zlim(0, np.max(heatmap) * 1.1)
ax.view_init(elev=45, azim=225)
plt.tight_layout()
plt.show()
