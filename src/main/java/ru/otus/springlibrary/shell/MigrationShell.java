package ru.otus.springlibrary.shell;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@ShellComponent
@AllArgsConstructor
public class MigrationShell {

    private final DataSource dataSource;

    private final JobLauncher jobLauncher;

    private final Job importUserJob;

    @ShellMethod("Start the migration")
    public void startMigration() {
        System.out.println("Starting the migration process...");
        try {
            jobLauncher.run(importUserJob, new JobParameters());
        } catch (Exception e) {
            System.out.println("Migration process cannot be finished due to the following error: " + e.getMessage());
        }
        System.out.println("Migration process finished successfully.");
    }

    @ShellMethod("Check database")
    public void checkDB() {
        System.out.println("Starting checking DB process...");
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            System.out.println("Check Authors table: ");
            ResultSet resultSet = statement.executeQuery("select * from test.authors;");

            StringBuilder tableData = new StringBuilder();
            while (resultSet.next()) {
                tableData.append("id: ")
                        .append(resultSet.getString("id"))
                        .append(", first_name: ")
                        .append(resultSet.getString("first_name"))
                        .append(", last_name: ")
                        .append(resultSet.getString("last_name"))
                        .append(System.lineSeparator());
            }

            String tableDataStr = tableData.toString();
            if (tableDataStr.isEmpty()) {
                System.out.println("Authors table is empty!");
            } else {
                System.out.println(tableDataStr);
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong during Authors table check: " + e.getMessage());
        }
    }
}
