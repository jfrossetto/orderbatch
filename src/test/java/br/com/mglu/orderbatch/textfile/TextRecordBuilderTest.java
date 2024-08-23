package br.com.mglu.orderbatch.textfile;

import br.com.mglu.orderbatch.order.OrderRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TextRecordBuilderTest {

    @InjectMocks
    private TextRecordBuilder textRecordBuilder;

    @Test
    void givenValidString_whenCallBuildRecord_shouldReturnOrderRecord() {
        OrderRecord orderRecord = textRecordBuilder
                .buildRecord("0000000075                                  Bobbie Batz00000007980000000002     1578.5720211116",
                             OrderRecord.class);
        assertNotNull(orderRecord);
        assertEquals("0000000075", orderRecord.userId());
        assertEquals("20211116", orderRecord.orderDate());
    }

}
