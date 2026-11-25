package mn.edu.must.sqat;

// ЛАБ 11 — хүчтэй болгосон тестийн багц.
// B1: сул assertion-уудыг ЯГ УТГЫН assertEquals болгосон (оролт нь starter-тай ижил).
// B2: зааг утгын тестүүд нэмсэн (доод хэсэг).

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipCalculatorTest {

    private static final double EPS = 1e-9;

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade ----

    @Test
    void letterGrade_returnsExactGradeForAllRanges() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---- gpa ----

    @Test
    void gpa_returnsExactAverage() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), EPS);
        assertEquals(1.5, calc.gpa(new double[]{75, 65}), EPS);
        assertEquals(0.0, calc.gpa(new double[]{40}), EPS);
    }

    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    // ---- scholarship ----

    @Test
    void scholarship_returnsExactPercent() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
        assertEquals(70, calc.scholarship(3.5, 13, false));
        assertEquals(50, calc.scholarship(3.1, 5, false));
        assertEquals(0, calc.scholarship(2.0, 1, false));
        assertEquals(0, calc.scholarship(4.0, 20, true));
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }

    // =====================================================================
    // B2 — ЗААГ УТГЫН ТЕСТҮҮД
    // =====================================================================

    // letterGrade: үсгэн дүн бүрийн заагийн хоёр тал + оролтын хязгаар 0 ба 100
    @ParameterizedTest(name = "letterGrade({0}) = {1}")
    @CsvSource({
            "100,   A",
            "90,    A",
            "89.99, B",
            "80,    B",
            "79.99, C",
            "70,    C",
            "69.99, D",
            "60,    D",
            "59.99, F",
            "0,     F"
    })
    void letterGrade_boundaries(double score, String expected) {
        assertEquals(expected, calc.letterGrade(score));
    }

    @Test
    void letterGrade_justOutsideRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-0.01));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(100.01));
    }

    // gpa: switch-ийн бүх үсгэн дүн тус тусдаа (A=4, B=3, C=2, D=1, F=0)
    @ParameterizedTest(name = "gpa([{0}]) = {1}")
    @CsvSource({
            "95, 4.0",
            "85, 3.0",
            "75, 2.0",
            "65, 1.0",
            "40, 0.0"
    })
    void gpa_eachLetterGrade(double score, double expected) {
        assertEquals(expected, calc.gpa(new double[]{score}), EPS);
    }

    @Test
    void gpa_allLettersTogether() {
        // (4 + 3 + 2 + 1 + 0) / 5 = 2.0
        assertEquals(2.0, calc.gpa(new double[]{90, 80, 70, 60, 59.99}), EPS);
    }

    // scholarship: 3.8/15, 3.4/12, 3.0 заагууд ба оролтын хязгаар (gpa 0 ба 4, credits 0)
    @ParameterizedTest(name = "scholarship({0}, {1}, false) = {2}")
    @CsvSource({
            // 100% дүрэм: gpa >= 3.8 ба credits >= 15
            "3.8,  15, 100",
            "3.79, 15, 70",
            "3.8,  14, 70",
            "4.0,  15, 100",
            // 70% дүрэм: gpa >= 3.4 ба credits >= 12
            "3.4,  12, 70",
            "3.39, 12, 50",
            "3.4,  11, 50",
            // 50% дүрэм: gpa >= 3.0
            "3.0,  0,  50",
            "2.99, 20, 0",
            // оролтын хязгаар
            "0.0,  0,  0",
            "4.0,  0,  50"
    })
    void scholarship_boundaries(double gpa, int credits, int expected) {
        assertEquals(expected, calc.scholarship(gpa, credits, false));
    }

    @Test
    void scholarship_violation_alwaysZero() {
        assertEquals(100, calc.scholarship(4.0, 20, false));
        assertEquals(0, calc.scholarship(4.0, 20, true));
    }

    @Test
    void scholarship_justOutsideRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.01, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.01, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }
}
