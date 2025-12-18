import repository.PatientRepository;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== PROGRAM STARTED ===");

        try {
            PatientRepository repo = new PatientRepository();
            repo.load("data/patients.csv");

            System.out.println("Patients loaded: " + repo.getAll().size());

            if (!repo.getAll().isEmpty()) {
                System.out.println("First patient:");
                System.out.println(repo.getAll().get(0));
            }

        } catch (Exception e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }

        System.out.println("=== PROGRAM FINISHED ===");
    }
}
