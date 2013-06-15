[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Tapping),
        "Tap"
    ) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{U}{U}")
            ];
        }
		
		@Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_CREATURE_WITHOUT_FLYING,
                new MagicTapTargetPicker(true,false),
                this,
                "Tap target creature\$ without flying."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, new MagicPermanentAction() {
                public void doAction(final MagicPermanent creature) {
                    game.doAction(new MagicTapAction(creature,true));
                }
            });
        }
    }
]
