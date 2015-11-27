[
    new EntersBattlefieldTrigger(MagicTrigger.REPLACEMENT) {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPayedCost payedCost) {     
            return new MagicEvent(
                permanent,
                this,
                "PN\$'s life total becomes 0."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player = event.getPlayer();
            final int amount = 0 - player.getLife();
            game.doAction(new ChangeLifeAction(player,amount));
        }
    },
    
    new IfPlayerWouldLoseTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final LoseGameAction loseAct) {
            if (permanent.isController(loseAct.getPlayer()) && loseAct.getReason() == LoseGameAction.LIFE_REASON) {
                loseAct.setPlayer(MagicPlayer.NONE);
            }
            return MagicEvent.NONE;
        }
    },
    
    new IfLifeWouldChangeTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final ChangeLifeAction act) {
            int amount = 0;
            if (permanent.isController(act.getPlayer()) && act.getLifeChange() > 0) {
                amount = act.getLifeChange();
                act.setLifeChange(0);
                return new MagicEvent(
                        permanent,
                        amount,
                        this,
                        "PN draws RN cards."
                    );
            }
            return MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new DrawAction(event.getPlayer(), event.getRefInt()));
        }
    },
    
    new IfLifeWouldChangeTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final ChangeLifeAction act) {
            final int amount = Math.abs(act.getLifeChange());
            return permanent.isController(act.getPlayer()) && act.getLifeChange() < 0 && act.isDamage() ?
                new MagicEvent(
                    permanent,
                    amount,
                    this,
                    "PN sacrifices RN nontoken permanents."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicEvent sac = new MagicRepeatedPermanentsEvent(
                event.getSource(),
                new MagicTargetChoice("a nontoken permanent to sacrifice"),
                event.getRefInt(),
                MagicChainEventFactory.Sac
            );
            if (sac.isSatisfied()) {
                game.addEvent(sac);
            } else {
                game.doAction(new LoseGameAction(event.getPlayer()," lost the game because of not being able to Sacrifice enough nontoken permanents."))
            }
        }
    },
    
    new ThisPutIntoGraveyardTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MoveCardAction act) {
            if (act.from(MagicLocationType.Battlefield) && act.to(MagicLocationType.Graveyard)) {
                game.doAction(new LoseGameAction(permanent.getController()," lost the game because of Lich entering the Graveyard."))
            }
            return MagicEvent.NONE;
        }
    }
    
]
