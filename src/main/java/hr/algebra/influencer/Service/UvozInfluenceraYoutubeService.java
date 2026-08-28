package hr.algebra.influencer.Service;

import hr.algebra.influencer.Task.UvozInfluenceraYoutubeTask;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UvozInfluenceraYoutubeService extends Service<Integer>
{

    @Override
    protected Task<Integer> createTask()
    {
        return new UvozInfluenceraYoutubeTask();
    }
}
