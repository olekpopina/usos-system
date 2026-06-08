package pl.usos.usossystem.repository;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentSemestrRepositoryTest {

    @Test
    void toDoubleConvertsBigDecimalFromDatabase() throws Exception {
        StudentSemestrRepository repository = new StudentSemestrRepository();

        Double result = invokeToDouble(repository, new BigDecimal("4.5"));

        assertEquals(4.5, result);
    }

    @Test
    void toDoubleReturnsNullForMissingGrade() throws Exception {
        StudentSemestrRepository repository = new StudentSemestrRepository();

        Double result = invokeToDouble(repository, null);

        assertNull(result);
    }

    @Test
    void toDoubleRejectsUnexpectedValueType() throws Exception {
        StudentSemestrRepository repository = new StudentSemestrRepository();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> invokeToDouble(repository, "4.5")
        );

        assertEquals("Nieoczekiwany typ oceny z bazy: java.lang.String", exception.getMessage());
    }

    private Double invokeToDouble(StudentSemestrRepository repository, Object value) throws Exception {
        Method method = StudentSemestrRepository.class.getDeclaredMethod("toDouble", Object.class);
        method.setAccessible(true);
        try {
            return (Double) method.invoke(repository, value);
        } catch (InvocationTargetException exception) {
            if (exception.getCause() instanceof Exception cause) {
                throw cause;
            }
            throw exception;
        }
    }
}
