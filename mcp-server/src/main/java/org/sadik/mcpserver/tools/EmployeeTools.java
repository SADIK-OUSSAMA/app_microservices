package org.sadik.mcpserver.tools;

import org.sadik.mcpserver.model.Employee;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
public class EmployeeTools {

    // Mock data - in production, this would come from a database
    private final List<Employee> employees = List.of(
            new Employee("Hassan", 12000.0, 4),
            new Employee("Fatima", 15000.0, 6),
            new Employee("Omar", 9000.0, 2),
            new Employee("Aisha", 18000.0, 8));

    @Bean
    @Description("Get details about an employee given their name")
    public Function<String, Employee> getEmployee() {
        return name -> employees.stream()
                .filter(emp -> emp.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(new Employee("Unknown", 0.0, 0));
    }

    @Bean
    @Description("Get a list of all employees in the company")
    public Function<Void, List<Employee>> getAllEmployees() {
        return ignored -> employees;
    }
}
