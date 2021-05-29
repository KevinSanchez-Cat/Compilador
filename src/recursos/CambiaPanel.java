package recursos;

import javax.swing.JPanel;

public class CambiaPanel {

    private final JPanel container;
    private final JPanel content;

    public CambiaPanel(JPanel container, JPanel content) {
        this.container = container;
        this.content = content;
        this.container.removeAll();
        this.container.revalidate();
        this.container.repaint();

        this.container.add(this.content);
        this.container.revalidate();
        this.container.repaint();
    }
}
