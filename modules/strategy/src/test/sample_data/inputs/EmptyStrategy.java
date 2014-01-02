import org.marketcetera.event.AskEvent;
import org.marketcetera.strategy.java.Strategy;

public class EmptyStrategy
        extends Strategy
{
    @Override
    public void onAsk(AskEvent ask)
    {
        setProperty("onAsk",
                    ask.toString());
    }
}