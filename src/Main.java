import repository.PatientRepository;

public class Main {
    public static void main(String[] args) {
        try {
            PatientRepository repo = new PatientRepository();
            repo.load("data/patients.csv");

            System.out.println("Loaded patients: " + repo.getAll().size());

            if (!repo.getAll().isEmpty()) {
                System.out.println("First patient: " + repo.getAll().get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
