package com.simulationthings;

import java.util.ArrayList;
import java.util.List;

// Ring buffer (cycle buffer) implementation for efficient memory management
// Stores up to maxSize elements with O(1) insertion, avoiding expensive array shifts
@SuppressWarnings("unchecked")
public class CycleBuffer<T> {
    
    private final T[] buffer;
    private int writeIndex = 0;      // Points to next write position
    private int count = 0;            // Number of elements currently in buffer
    private final int maxSize;

    public CycleBuffer(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be greater than 0");
        }
        this.maxSize = maxSize;
        this.buffer = (T[]) new Object[maxSize];
    }

    // Add element to the buffer. If full, overwrites oldest element.
    // O(1) operation - no array shifting
    public void add(T element) {
        buffer[writeIndex] = element;
        writeIndex = (writeIndex + 1) % maxSize;
        if (count < maxSize) {
            count++;
        }
    }

    // Get element at index. Index 0 is oldest element, count-1 is newest.
    // Returns null if index is out of bounds
    public T get(int index) {
        if (index < 0 || index >= count) {
            return null;
        }
        int actualIndex = (writeIndex - count + index + maxSize) % maxSize;
        return buffer[actualIndex];
    }

    // Remove and return the last (newest) element. Returns null if empty.
    public T removeLast() {
        if (count == 0) {
            return null;
        }
        writeIndex = (writeIndex - 1 + maxSize) % maxSize;
        count--;
        T element = buffer[writeIndex];
        buffer[writeIndex] = null; // Help garbage collection
        return element;
    }

    // Check if buffer is empty
    public boolean isEmpty() {
        return count == 0;
    }

    // Get current number of elements in buffer
    public int size() {
        return count;
    }

    // Get maximum capacity of buffer
    public int getMaxSize() {
        return maxSize;
    }

    // Check if buffer is full
    public boolean isFull() {
        return count == maxSize;
    }

    // Clear all elements from buffer
    public void clear() {
        for (int i = 0; i < maxSize; i++) {
            buffer[i] = null;
        }
        writeIndex = 0;
        count = 0;
    }

    // Get all elements as a list (from oldest to newest)
    public List<T> toList() {
        List<T> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(get(i));
        }
        return result;
    }
}
