package com.demopanel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BlockTest {

    @Test
    void unpaintedBlockToStringReturnsZero(){
        Block block = new Block(0,0,null);
        assertEquals(" 0 ",block.toString());
    }

    @Test
    void paintedBlockToStringReturnsOne(){
        Block block = new Block(0,0,null);
        block.paint();
        assertEquals(" 1 ",block.toString());
    }

    @Test
    void isPaintedFalseByDeafult(){
        Block block = new Block(0,0,null);
        assertFalse(block.isPainted);
    }

    @Test
    void isPaintedTrueAfterPainting(){
        Block block = new Block(0,0,null);
        block.paint();
        assertTrue(block.isPainted);
    }

}
