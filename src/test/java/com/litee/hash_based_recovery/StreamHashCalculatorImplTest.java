package com.litee.hash_based_recovery;

import com.litee.hash_based_recovery.hashing.Hashes;
import com.litee.hash_based_recovery.hashing.StreamHashCalculator;
import com.litee.hash_based_recovery.hashing.StreamHashCalculatorImpl;
import jonelo.sugar.util.Base32;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class StreamHashCalculatorImplTest {
    @Test
    public void testEmptyFile() throws IOException {
        StreamHashCalculator instance = createInstanceToTest();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{0x74, 0x65, 0x73, 0x74});
        Hashes result = instance.calculateHashes(inputStream);
        Assert.assertEquals("B6JXUSY53GRPEMBFLR32TVQ5AEXDOEBHJAFG5LQ", Base32.encode(result.getTTH()));
    }

    public StreamHashCalculator createInstanceToTest() {
        return new StreamHashCalculatorImpl();
    }
}