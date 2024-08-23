package br.com.mglu.orderbatch.order;

import br.com.mglu.orderbatch.textfile.TextRecordBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderRecordProcessorTest {

    private static final String LINE_1 = "0000000049                               Ken Wintheiser00000005230000000003      586.7420210903";
    private static final String LINE_2 = "0000000014                                 Clelia Hills00000001460000000001      673.4920211125";
    private static final String LINE_3 = LINE_2;

    @Spy
    private TextRecordBuilder textProcessor = new TextRecordBuilder();
    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderRecordProcessor processor;

    @Test
    void givenInputStream_whenCallProcessFile_shouldAddToDbAndReturnNotInclued() {
        InputStream fileStream = buildFileStream(List.of(LINE_1, LINE_2, LINE_3));
        List<String> notinluded = processor.processFile(fileStream);
        assertNotNull(notinluded);
        assertEquals(1, notinluded.size());
        assertEquals(LINE_3, notinluded.getFirst());
        verify(repository).insertUsers(anyList());
        verify(repository).insertOrders(anyList());
        ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
        verify(repository).insertOrderProducts(captor.capture());
        List<OrderProduct> orderProducts = captor.getValue().stream().toList();
        assertEquals(2, orderProducts.size());
        assertTrue(orderProducts.stream()
                        .anyMatch(orderProduct -> BigDecimal.valueOf(586.74).compareTo(orderProduct.getProductValue()) == 0));
        assertTrue(orderProducts.stream()
                .anyMatch(orderProduct -> BigDecimal.valueOf(673.49).compareTo(orderProduct.getProductValue()) == 0));
    }

    private InputStream buildFileStream(List<String> lines) {
        StringBuffer linesBuffer = new StringBuffer();
        lines.forEach(line -> linesBuffer.append(line).append(System.lineSeparator()));
        byte[] bytes = linesBuffer.toString().getBytes();
        return new ByteArrayInputStream(bytes);
    }

}
