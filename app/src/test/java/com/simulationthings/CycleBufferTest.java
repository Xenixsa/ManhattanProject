package com.simulationthings;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CycleBufferTest {

    @Test
    public void testAddAndGet() {
        CycleBuffer<Integer> buffer = new CycleBuffer<>(3);
        
        buffer.add(10);
        buffer.add(20);
        buffer.add(30);
        
        assertEquals(10, buffer.get(0)); // oldest
        assertEquals(20, buffer.get(1));
        assertEquals(30, buffer.get(2)); // newest
        assertEquals(3, buffer.size());
    }

    @Test
    public void testOverwrite() {
        CycleBuffer<Integer> buffer = new CycleBuffer<>(3);
        
        buffer.add(10);
        buffer.add(20);
        buffer.add(30);
        buffer.add(40); // overwrites 10
        
        // Now we have 20, 30, 40
        assertEquals(20, buffer.get(0)); // oldest
        assertEquals(30, buffer.get(1));
        assertEquals(40, buffer.get(2)); // newest
        assertEquals(3, buffer.size());
    }

    @Test
    public void testRemoveLast() {
        CycleBuffer<Integer> buffer = new CycleBuffer<>(5);
        
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        
        assertEquals(3, buffer.removeLast());
        assertEquals(2, buffer.removeLast());
        assertEquals(1, buffer.removeLast());
        assertNull(buffer.removeLast());
        
        assertTrue(buffer.isEmpty());
    }

    @Test
    public void testMementoUsage() {
        CycleBuffer<String> history = new CycleBuffer<>(300);
        
        // Simulate saving 5 mementos
        for (int i = 0; i < 5; i++) {
            history.add("State-" + i);
        }
        
        assertEquals(5, history.size());
        
        // Simulate undoing 3 steps
        assertEquals("State-4", history.removeLast());
        assertEquals("State-3", history.removeLast());
        assertEquals("State-2", history.removeLast());
        
        assertEquals(2, history.size());
        assertEquals("State-0", history.get(0));
        assertEquals("State-1", history.get(1));
    }

    @Test
    public void testEmptyBuffer() {
        CycleBuffer<Integer> buffer = new CycleBuffer<>(5);
        
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
        assertNull(buffer.removeLast());
        assertNull(buffer.get(0));
    }

    @Test
    public void testClear() {
        CycleBuffer<Integer> buffer = new CycleBuffer<>(3);
        
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        
        buffer.clear();
        
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
        assertNull(buffer.get(0));
    }

    @Test
    public void testIsFull() {
        CycleBuffer<Integer> buffer = new CycleBuffer<>(3);
        
        assertFalse(buffer.isFull());
        
        buffer.add(1);
        assertFalse(buffer.isFull());
        
        buffer.add(2);
        assertFalse(buffer.isFull());
        
        buffer.add(3);
        assertTrue(buffer.isFull());
    }

    @Test
    public void testInvalidIndex() {
        CycleBuffer<Integer> buffer = new CycleBuffer<>(3);
        
        buffer.add(10);
        buffer.add(20);
        
        assertNull(buffer.get(-1));
        assertNull(buffer.get(2));
        assertNull(buffer.get(100));
    }
}
