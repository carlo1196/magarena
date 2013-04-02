package magic.card;

import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.action.MagicChangeTurnPTAction;
import magic.model.event.MagicEvent;
import magic.model.target.MagicTargetFilter;
import magic.model.trigger.MagicWhenAttacksTrigger;

import java.util.Collection;

public class Goblin_Piledriver {
    public static final MagicWhenAttacksTrigger T = new MagicWhenAttacksTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent creature) {
            return (permanent==creature) ?
                new MagicEvent(
                    permanent,
                    this,
                    "SN gets +2/+0 until end of turn for each other attacking Goblin."):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(
                final MagicGame game,
                final MagicEvent event) {
            final MagicPermanent creature=event.getPermanent();
            final Collection<MagicPermanent> targets=
                game.filterPermanents(creature.getController(),MagicTargetFilter.TARGET_ATTACKING_GOBLIN);
            //excluding itself
            final int power = 2 * (targets.size() - 1);
            if (power>0) {
                game.doAction(new MagicChangeTurnPTAction(creature,power,0));
            }
        }
    };
}
