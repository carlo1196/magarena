package magic.ui;

import magic.data.DuelConfig;
import magic.data.GeneralConfig;
import magic.data.IconImages;
import magic.data.OSXAdapter;
import magic.model.MagicCardList;
import magic.model.MagicDeck;
import magic.model.MagicDeckConstructionRule;
import magic.model.MagicDuel;
import magic.model.MagicGame;
import magic.model.MagicGameLog;
import magic.model.MagicPlayerDefinition;
import magic.test.TestGameBuilder;
import magic.ui.screen.CardExplorerScreen;
import magic.ui.screen.CardZoneScreen;
import magic.ui.screen.DeckEditorScreen;
import magic.ui.screen.DuelDecksScreen;
import magic.ui.screen.DuelGameScreen;
import magic.ui.screen.HelpMenuScreen;
import magic.ui.screen.KeywordsScreen;
import magic.ui.screen.AbstractScreen;
import magic.ui.screen.SampleHandScreen;
import magic.ui.screen.SettingsMenuScreen;
import magic.ui.screen.MainMenuScreen;
import magic.ui.screen.ReadmeScreen;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Stack;

@SuppressWarnings("serial")
public class MagicFrame extends JFrame {

    private boolean ignoreWindowDeactivate;
    private boolean confirmQuitToDesktop = true;

    private static final Dimension MIN_SIZE = new Dimension(GeneralConfig.DEFAULT_WIDTH, GeneralConfig.DEFAULT_HEIGHT);

    // Add "-DtestGame=X" VM argument to start a TestGameBuilder game
    // where X is one of the classes (without the .java) in "magic.test".
    private static final String testGame = System.getProperty("testGame");

    // Check if we are on Mac OS X.  This is crucial to loading and using the OSXAdapter class.
    public static final boolean MAC_OS_X = System.getProperty("os.name").toLowerCase().startsWith("mac os x");

    private final GeneralConfig config;
    private final JPanel contentPanel;
    private MagicDuel duel;
    private final Stack<AbstractScreen> screens;
    private final boolean dontShowAgain = true;

    public MagicFrame(final String frameTitle) {

        // Load settings.
        config = GeneralConfig.getInstance();
        config.load();

        // Setup frame.
        this.setTitle(frameTitle + "  [F11 : full screen]");
        this.setIconImage(IconImages.ARENA.getImage());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListeners();
        registerForMacOSXEvents();
        setSizeAndPosition();

        // Setup content container with a painted background based on theme.
        contentPanel = new BackgroundPanel(new MigLayout("insets 0, gap 0"));
        contentPanel.setOpaque(true);
        setContentPane(contentPanel);
        setF11KeyInputMap();
        setF12KeyInputMap();

        // First screen to display is the main menu.
        screens = new Stack<AbstractScreen>();
        showMainMenuScreen();
        if (testGame != null) {
            openGame(TestGameBuilder.buildGame(testGame));
        }

        setVisible(true);

        // add "-DselfMode=true" VM argument for AI vs AI mode.
        // in selfMode start game immediately based on configuration from duel.cfg
        if (Boolean.getBoolean("selfMode")) {
            final DuelConfig config=DuelConfig.getInstance();
            config.load();
            newDuel(config);
        }

    }

    //
    // The various (Mag)screens that can currently be displayed.
    //
    public void showCardZoneScreen(final MagicCardList cards, final String zoneName, final boolean animateCards) {
        activateMagScreen(new CardZoneScreen(this, cards, zoneName, animateCards));
    }
    public void showSampleHandGenerator(final MagicDeck deck) {
        activateMagScreen(new SampleHandScreen(this, deck));
    }
    public void showDeckEditor() {
        activateMagScreen(new DeckEditorScreen(this));
    }
    public void showDeckEditor(final MagicDeck deck) {
        activateMagScreen(new DeckEditorScreen(this, deck));
    }
    public void showCardExplorerScreen() {
        activateMagScreen(new CardExplorerScreen(this));
    }
    public void showReadMeScreen() {
        activateMagScreen(new ReadmeScreen(this));
    }
    public void showKeywordsScreen() {
        activateMagScreen(new KeywordsScreen(this));
    }
    public void showHelpMenuScreen() {
        activateMagScreen(new HelpMenuScreen(this));
    }
    public void showSettingsMenuScreen() {
        activateMagScreen(new SettingsMenuScreen(this));
    }
    private void showDuelDecksScreen() {
        if (screens.peek() instanceof DuelDecksScreen) {
            screens.pop();
        }
        activateMagScreen(new DuelDecksScreen(this, duel));
    }
    public void showMainMenuScreen() {
        screens.clear();
        activateMagScreen(new MainMenuScreen(this));
    }
    private void activateMagScreen(final AbstractScreen screen) {
        showMagScreen(screen);
        screens.push(screen);
        screen.requestFocus();
    }
    private void showMagScreen(final AbstractScreen screen) {
        contentPanel.removeAll();
        contentPanel.add(screen, "w 100%, h 100%");
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    public void closeActiveScreen(final boolean isEscapeKeyAction) {
        if (screens.size() == 1) {
            quitToDesktop(isEscapeKeyAction);
        } else {
            final AbstractScreen activeScreen = screens.pop();
            if (activeScreen.isScreenReadyToClose(screens.peek())) {
                showMagScreen(screens.peek());
            } else {
                screens.push(activeScreen);
            }
        }
    }

    private void addWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                onClose();
            }
            @Override
            public void windowDeactivated(final WindowEvent e) {
                if (isFullScreen() && e.getOppositeWindow() == null && !ignoreWindowDeactivate) {
                    setState(Frame.ICONIFIED);
                }
                ignoreWindowDeactivate = false;
            }
        });
    }

    public void showDuel() {
        if (duel!=null) {
            showDuelDecksScreen();
            if (Boolean.getBoolean("selfMode")) {
                if (!duel.isFinished()) {
                    nextGame();
                } else {
                    newDuel(DuelConfig.getInstance());
                }
            }
        }
    }

    public void showNewDuelDialog() {
        new DuelDialog(this);
    }

    public void newDuel(final DuelConfig configuration) {
        duel = new MagicDuel(configuration);
        duel.initialize();
        showDuel();
    }

    public void loadDuel() {
        final File duelFile=MagicDuel.getDuelFile();
        if (duelFile.exists()) {
            duel=new MagicDuel();
            duel.load(duelFile);
            showDuel();
        } else {
            JOptionPane.showMessageDialog(this, "No saved duel found.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void restartDuel() {
        if (duel!=null) {
            duel.restart();
            showDuel();
        }
    }

    public boolean isLegalDeckAndShowErrors(final MagicDeck deck, final String playerName) {
        final String brokenRulesText =
                MagicDeckConstructionRule.getRulesText(MagicDeckConstructionRule.checkDeck(deck));

        if(brokenRulesText.length() > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    playerName + "'s deck is illegal.\n\n" + brokenRulesText,
                    "Illegal Deck",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public void nextGame() {
        duel.updateDifficulty();
        final MagicPlayerDefinition[] players=duel.getPlayers();
        if(isLegalDeckAndShowErrors(players[0].getDeck(), players[0].getName()) &&
           isLegalDeckAndShowErrors(players[1].getDeck(), players[1].getName())) {
            openGame(duel.nextGame(true));
        }
    }

    private void openGame(final MagicGame game) {
        activateMagScreen(new DuelGameScreen(this, game));
    }

    /**
     * Set up our application to respond to the Mac OS X application menu
     */
    private void registerForMacOSXEvents() {
        if (MAC_OS_X) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("onClose"));
                //OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
                //OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
                //OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }

    public boolean onClose() {
        if (!confirmQuitToDesktop) {
            doShutdownMagarena();
        } else {
            final String message = "Are you sure you want to quit Magarena?\n";
            final Object[] params = {message};
            final int n = JOptionPane.showConfirmDialog(
                    contentPanel,
                    params,
                    "Confirm Quit to Desktop",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                doShutdownMagarena();
            }
        }
        // set the ApplicationEvent as handled (for OS X)
        return false;
    }

    private void doShutdownMagarena() {
        if (isFullScreen()) {
            config.setFullScreen(true);
        } else {
            final boolean maximized = (MagicFrame.this.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
            if (maximized) {
                config.setMaximized(true);
            } else {
                config.setLeft(getX());
                config.setTop(getY());
                config.setWidth(getWidth());
                config.setHeight(getHeight());
                config.setMaximized(false);
            }
        }
        config.setConfirmExit(!dontShowAgain);
        config.save();

        MagicGameLog.close();

        /*
        if (gamePanel != null) {
            gamePanel.getController().haltGame();
        }
        */
        System.exit(0);
    }

    public void quitToDesktop(final boolean confirmQuit) {
        this.confirmQuitToDesktop = confirmQuit;
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void updateGameView() {
        if (screens.peek() instanceof DuelGameScreen) {
            final DuelGameScreen screen = (DuelGameScreen)screens.peek();
            screen.updateView();
        }
    }

    public void openPreferencesDialog() {
        new PreferencesDialog(this);
    }

    private void setSizeAndPosition() {
        setMinimumSize(MIN_SIZE);
        if (config.isFullScreen()) {
            setFullScreenMode(true);
        } else {
            this.setSize(config.getWidth(),config.getHeight());
            if (config.getLeft()!=-1) {
                this.setLocation(config.getLeft(),config.getTop());
            } else {
                this.setLocationRelativeTo(null);
            }
            if (config.isMaximized()) {
                this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        }
    }

    /**
     *
     */
    public void closeDuelScreen() {
        closeActiveScreen(false);
        showDuel();
    }

    public void toggleFullScreenMode() {
        setFullScreenMode(!config.isFullScreen());
    }

    private void setFullScreenMode(final boolean isFullScreen) {
        this.dispose();
        if (isFullScreen) {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.setUndecorated(true);
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.setSize(screenSize.width, screenSize.height);
            config.setFullScreen(true);
        } else {
            this.setExtendedState(JFrame.NORMAL);
            this.setUndecorated(false);
            config.setFullScreen(false);
            setSizeAndPosition();
        }
        setVisible(true);
        ignoreWindowDeactivate = true;
        config.save();
    }

    private boolean isFullScreen() {
        return isMaximized() && this.isUndecorated();
    }

    private boolean isMaximized() {
        return this.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    }

    /**
     * F11 key toggles full screen mode.
     */
    private void setF11KeyInputMap() {
        contentPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "FullScreen");
        contentPanel.getActionMap().put("FullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                toggleFullScreenMode();
            }
        });
    }

    private void setF12KeyInputMap() {
        contentPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F12"), "HideMenu");
        contentPanel.getActionMap().put("HideMenu", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //menuPanel.setVisible(!menuPanel.isVisible());
                final AbstractScreen activeScreen = screens.peek();
                activeScreen.setVisible(!activeScreen.isVisible());
            }
        });
    }

}
