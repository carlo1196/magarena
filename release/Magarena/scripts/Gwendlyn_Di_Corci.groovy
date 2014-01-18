def YOUR_TURN_CONDITION = new MagicCondition() {
    public boolean accept(final MagicSource source) {
        final MagicGame game = source.getGame();
        return source.getController() == game.getTurnPlayer();
    }
};

[
    new MagicPermanentActivation(
        [
            YOUR_TURN_CONDITION,
        ],
        new MagicActivationHints(MagicTiming.Main),
        "Discard"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source)
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_PLAYER,
                this,
                "Target player\$ discards a card at random."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game, {
                final MagicPlayer player ->
                game.addEvent(MagicDiscardEvent.Random(event.getSource(),player));
            });
        }
    }
]
