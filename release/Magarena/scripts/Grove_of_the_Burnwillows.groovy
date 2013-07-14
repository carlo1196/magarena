[
    new MagicManaActivation(MagicManaType.getList("{R} or {G}")) {
        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent perm) {
            return [
                new MagicTapEvent(perm),
                new MagicGainLifeEvent(perm, perm.getOpponent(), 1)
            ];
        }
    }
]
