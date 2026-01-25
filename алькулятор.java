 import java.util.Scanner;
class Main{
    public static void main(String[]args){
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Ведите первое число");
            double num1 = sc.nextDouble();
            System.out.println("Ведите оператор (+, -, *, /):");
                    char operator = sc.next().charAt(0);
            System.out.println("Ведите второе число");
            double num2 = sc.nextDouble();
            double result = 0;
            boolean error = false;
            switch(operator){
                case '+':
                  result = num1 + num2;
                case '-':
                    result = num1 - num2;
                    break;
                case '*':
                    result = num1 * num2;
                    break;
                case '/':
                    if(num2 == 0) {
                        System.out.println("Нельзя делить на ноль");
                        error = true;
                    }else {
                        result = num1 / num2;
                        break;
                    }
                default:
                    System.out.println("Неизвестная операция");
                    error = true;
                    }
                    if(!error){
                        System.out.println("Результат"+ result);
            }

        }
    }
}

