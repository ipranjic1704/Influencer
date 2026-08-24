package hr.algebra.influencer.Service;

import hr.algebra.influencer.Task.UvozGradovaTask;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UvozGradovaService extends Service<Integer>
{

    @Override
    protected Task<Integer> createTask()
    {
        return new UvozGradovaTask();
    }
}
