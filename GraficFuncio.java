/*
 * Copyright (C) Joan Bruguera 2010
 *
 * This file is part of CalcBatx.
 *
 * CalcBatx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CalcBatx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CalcBatx.  If not, see <http://www.gnu.org/licenses/>.
 */

package calcbatx;

import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
/**
 * "JPanel" personalitzat que permet dibuixar-hi una funció.
 */
public class GraficFuncio extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    private double iniciX, zonaX, iniciY, zonaY;
    private Funcio funcio, funcioDerivada;
    private boolean mostrarRe, mostrarIm, mostrarTg;

    public GraficFuncio() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        reiniciar();
    }

    public void reiniciar() {
        iniciX = -10.0;
        zonaX = 20.0;

        iniciY = -10.0;
        zonaY = 20.0;

        mostrarRe = true;
        mostrarIm = false;
        mostrarTg = true;
    }

    /**
     * Estableix la funció a dibuixar.
     * Un valor "null" significa que no es dibuixarà res.
     */
    public void setFuncio(Funcio funcio) {
        // TODO Arreglar
        this.funcio = funcio;
        this.funcioDerivada = null;
        try {
            this.funcioDerivada = new FuncioUsuari("f'(x)", 1, funcio.derivada(new Expressio[] { new ExpressioParametre(0) }), "");
        } catch (Exception ex) { }
        repaint();
    }

    @Override public void paintComponent(Graphics g) {
        //((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);

        // Eixos
        g.setColor(Color.BLACK);
        g.drawLine(0, coordY(0), getWidth(), coordY(0)); // X
        g.drawLine(coordX(0), 0, coordX(0), getHeight()); // Y

        // Divisions entre eixos (Per fer: Esborrar codi extra)
        g.setFont(new Font("SansSerif", Font.PLAIN, 8));

        //DecimalFormat formatCientifica = new DecimalFormat("0.####E0");
        DecimalFormat formatCientifica = new DecimalFormat("0.####");
        double passaX = (zonaX / 10);
        for (int i = 0; i < 10; i++)
        {
            double x = iniciX + passaX * i + passaX / 2;
            g.drawLine(coordX(x), coordY(0) - 5, coordX(x), coordY(0) + 5);
            g.drawString(formatCientifica.format(x), coordX(x) + 2, coordY(0) - 2);
        }

        double passaY = (zonaY / 10);
        for (int i = 0; i < 10; i++)
        {
            double y = iniciY + passaY * i + passaY / 2;
            g.drawLine(coordX(0) - 5, coordY(y), coordX(0) + 5, coordY(y));
            g.drawString(formatCientifica.format(y), coordX(0) + 5, coordY(y) + 2);
        }
        
        if (funcio != null) {
            // Calcular tots els punts a representar
            Complex[] punts = new Complex[getWidth()+1]; // Punt extra per l'enllaç final
            for (int i = 0; i < punts.length; i++)
                punts[i] = YenPos(i);

            // Dibuixar punts reals
            if (mostrarRe) {
                g.setColor(Color.BLUE);
                for (int i = 0; i < punts.length - 1; i++) {
                    unirPunts(g, i, punts[i].re(), i+1, punts[i+1].re());
                }
            }

            // Dibuixar punts imaginaris
            if (mostrarIm) {
                g.setColor(Color.RED);
                for (int i = 0; i < punts.length - 1; i++)
                    unirPunts(g, i, punts[i].im(), i+1, punts[i+1].im());
            }
            
            g.setColor(Color.MAGENTA.darker());
            if (posRatoliMou != null && yEnGrafic(punts[posRatoliMou.x].re())) {
                // Dibuixar punt sobre el ratoli
                marcarPunt(g, posRatoliMou.x, punts[posRatoliMou.x].re());

                // Dibuixar recta tangent
                if (mostrarTg) {
                    Complex m = Complex.NaN;
                    try {
                        m = funcioDerivada.calcular(new Complex[] { new Complex(XenPos(posRatoliMou.x)) });
                    } catch (Exception ex) { }
                    if (Nombre.esFinit(m.re())) {
                        double x = XenPos(posRatoliMou.x);
                        double y = punts[posRatoliMou.x].re();
                        double x1 = XenPos(0);
                        double x2 = XenPos(getWidth());
                        double y1 = m.re() * (x1 - x) + y;
                        double y2 = m.re() * (x2 - x) + y;
                        g.drawLine(0, coordY(y1), getWidth(), coordY(y2));
                    }
                }
            }
        }
    }

    /**
     * Marca un punt en el gràfic amb un cercle.
     * @param g El context gràfic.
     * @param x La coordenada X en el gràfic del punt.
     * @param y El valor d'y de la funció en el punt.
     */
    private void marcarPunt(Graphics g, int x, double y) {
        int yg = coordY(y);

        g.drawOval(x - 3, yg - 3, 6, 6);
        g.fillOval(x - 3, yg - 3, 6, 6);
    }

    /**
     * Uneix dos punts amb una recta, asseguntant que la unió sigui vàlida.
     * @param g El context gràfic.
     * @param x1 La coordenada X en el gràfic del primer punt.
     * @param y1 El valor d'y de la funció en el primer punt.
     * @param x2 La coordenada X en el gràfic del segon punt.
     * @param y2 El valor d'y de la funció en el segon punt.
     */
    private void unirPunts(Graphics g, int x1, double y1, int x2, double y2) {
        // Amb aquestes comprovacions s'eviten la majoria d'assimptotes
        // NOTA: No es pot fer yEnGrafic(), ja que 2 baixos o 2 alts s'accepta
        if (Double.isInfinite(y1) || Double.isNaN(y1) || // y1 no real
            Double.isInfinite(y2) || Double.isNaN(y2) || // y2 no real
            (y1 < iniciY && y2 > (iniciY + zonaY)) ||    // y1 baix i y2 alt
            (y2 < iniciY && y1 > (iniciY + zonaY)))      // y2 baix i y1 alt
            return;

        g.drawLine(x1, coordY(y1), x2, coordY(y2));
    }

    /**
     * Comprova si el valor d'y especificat entra dins del gràfic.
     * @param y El valor d'y de la funció.
     * @return true si el valor d'y entra en el gràfic.
     */
    private boolean yEnGrafic(double y) {
        return (y > iniciY && y < (iniciY + zonaY));
    }

    /**
     * Obtè el valor de la component X en la posició especificada.
     * @param pix El pixel.
     * @return El valor de la component X.
     */
    public double XenPos(int pix) {
        return iniciX + pix * zonaX / getWidth();
    }

    /**
     * Obtè el valor de la component Y en la posició especificada.
     * @param pix El píxel.
     * @return El valor de la component Y.
     */
    public Complex YenPos(int pix) {
        if (funcio == null)
            return Complex.NaN;

        try {
            return funcio.calcular(new Complex[] { new Complex(XenPos(pix)) });
        } catch (ExpressioException exception) {
            return Complex.NaN;
        }
    }
    
    /**
     * Calcular la coordenada X en el gràfic d'un valor x de la funció.
     * @param x El valor d'x de la funció.
     * @return La coordenada del valor d'x en el gràfic.
     */
    private int coordX(double x) {
        return (int)((x - iniciX) * getWidth() / zonaX);
    }

    /**
     * Calcular la coordenada Y en el gràfic d'un valor y de la funció.
     * @param x El valor d'y de la funció.
     * @return La coordenada del valor d'y en la gràfic.
     */
    private int coordY(double y) {
        return getHeight() - (int)((y - iniciY) * getHeight() / zonaY);
    }
    
    /**
     * Desplaçar l'eix X.
     * @param escala El factor (tant per u) a desplaçar respecte a el tamany actual.
     */
    public void desplacarX(double escala) {
        iniciX += zonaX * escala;
        repaint();
    }
    
    /**
     * Desplaçar l'eix Y.
     * @param escala El factor (tant per u) a desplaçar respecte a el tamany actual.
     */
    public void desplacarY(double escala) {
        iniciY += zonaY * escala;
        repaint();
    }
    
    /**
     * Amplia o redueix el gràfic (a partir del centre).
     * @param escala Factor d'ampliació. Per exemple:
     * escala = 2 significa que es veurà la zona del centre 2 vegades més gran.
     * escala = 0.5 significa que es veurà la zona del centre 2 vegades més petita.
     */
    public void ampliar(double escala) {
        // Al fer la zona N cops més gran, contindrà 1/N valors.
        zonaX *= 1 / escala;
        zonaY *= 1 / escala;
        
        // Modificar la posició inicial per tal de que la nova zona quedi centrada
        // Es pot obtenir la fòrmula aillant marge en: zona * escala = marge + zona + marge
        iniciX += zonaX * (escala - 1) / 2;
        iniciY += zonaY * (escala - 1) / 2;
        
        repaint();
    }

    Point posRatoliClic = null;
    Point posRatoliMou = null;

    @Override protected void processMouseEvent(MouseEvent evt)
    {
        if (evt.isPopupTrigger()) {
            JPopupMenu menu = new JPopupMenu("Gràfic");
            
            JCheckBoxMenuItem botoMostrarRe = new JCheckBoxMenuItem("Mostrar part real", mostrarRe);
            botoMostrarRe.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    mostrarRe = !mostrarRe;
                    repaint();
                }
            });

            JCheckBoxMenuItem botoMostrarIm = new JCheckBoxMenuItem("Mostrar part imaginària", mostrarIm);
            botoMostrarIm.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    mostrarIm = !mostrarIm;
                    repaint();
                }
            });
            
            JCheckBoxMenuItem botoMostrarTg = new JCheckBoxMenuItem("Mostrar tangent", mostrarTg);
            botoMostrarTg.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    mostrarTg = !mostrarTg;
                    repaint();
                }
            });

            JMenuItem botoReiniciar = new JMenuItem("Reiniciar");
            botoReiniciar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    reiniciar();
                    repaint();
                }
            });

            menu.add(botoMostrarRe);
            menu.add(botoMostrarIm);
            menu.add(botoMostrarTg);
            menu.add(botoReiniciar);
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        super.processMouseEvent(evt);
    }

    public void mouseClicked(MouseEvent evt) {
        requestFocus(); // Per permetre l'interacció amb el teclat
    }

    public void mousePressed(MouseEvent evt) {
        posRatoliClic = evt.getPoint();
    }

    public void mouseReleased(MouseEvent evt) {
        posRatoliClic = null;
    }

    public void mouseEntered(MouseEvent evt) {
        mouseMoved(evt);
    }

    public void mouseExited(MouseEvent evt) {
        posRatoliMou = null;
        repaint();
    }

    public void mouseDragged(MouseEvent evt) {
        Point posRatoliNova = evt.getPoint();
        desplacarX((double)-(posRatoliNova.x - posRatoliClic.x) / getWidth());
        desplacarY((double)(posRatoliNova.y - posRatoliClic.y) / getHeight());
        posRatoliClic = posRatoliNova;
    }

    public void mouseMoved(MouseEvent evt) {
        setToolTipText(String.format(
            "f(%1$s)=%2$s",
            Double.toString(XenPos(evt.getX())),
            YenPos(evt.getX()).toString()
        ));

        posRatoliMou = evt.getPoint();
        repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent evt) {
        if (evt.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
            ampliar(Math.pow(1.1, -evt.getUnitsToScroll()));
    }

    public void keyTyped(KeyEvent evt) { }

    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_LEFT)
            desplacarX(-0.025);
        else if (evt.getKeyCode() == KeyEvent.VK_RIGHT)
            desplacarX(0.025);
        else if (evt.getKeyCode() == KeyEvent.VK_DOWN)
            desplacarY(-0.025);
        else if (evt.getKeyCode() == KeyEvent.VK_UP)
            desplacarY(0.025);
        else if (evt.getKeyCode() == KeyEvent.VK_ADD)
            ampliar(1.1);
        else if (evt.getKeyCode() == KeyEvent.VK_SUBTRACT)
            ampliar(1/1.1);
    }

    public void keyReleased(KeyEvent evt) { }
}
