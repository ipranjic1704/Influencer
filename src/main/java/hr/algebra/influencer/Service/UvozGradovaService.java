package hr.algebra.influencer.Service;

import hr.algebra.influencer.Task.UvozGradovaTask;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

// JavaFX Service koji pokrece uvoz gradova s API-ja u pozadinskom threadu.
// Instanca se cuva i rekoristi - pozivati restart() za ponavljanje uvoza.
public class UvozGradovaService extends Service<Integer> {

    @Override
    protected Task<Integer> createTask() {
        return new UvozGradovaTask();
    }
}
