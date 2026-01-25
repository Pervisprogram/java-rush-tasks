
    class Solution {
        public static void main(String[] args) {
            // Создаем двумерный массив для хранения показаний (2 строки, 5 столбцов)
            int[][] sensorData = new int[2][5];
            // Начальное значение для заполнения массива
            int s = 10;

            // Заполняем массив по строкам, слева направо
            for (int i = 0; i < sensorData.length; i++) {
                for (int j = 0; j < sensorData[i].length; j++) {
                    sensorData[i][j] = s++;
                }
            }
            for (int i = 0; i < sensorData.length; i++) {
                for (int j = 0; j < sensorData[i].length; j++) {
                    System.out.print(sensorData[i][j]);
                    if (j < sensorData[i].length - 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }
    }






            // Выводим массив в виде таблицы


            // Печатаем значение элемента

            // Если это не последний элемент в строке, добавляем пробел


