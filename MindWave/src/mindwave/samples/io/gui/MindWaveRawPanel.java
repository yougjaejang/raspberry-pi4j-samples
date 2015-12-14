package mindwave.samples.io.gui;

import gnu.io.CommPortIdentifier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.NumberFormat;

import java.util.Map;

import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JScrollPane;

import javax.swing.JTextArea;

import mindwave.samples.io.gui.ctx.MindWaveContext;
import mindwave.samples.io.gui.ctx.MindWaveListener;
import mindwave.samples.io.gui.widgets.HalfDisplay;

import util.DumpUtil;

public class MindWaveRawPanel
  extends JPanel
{
  private BorderLayout borderLayout1 = new BorderLayout();
  private JScrollPane jScrollPane = new JScrollPane();
  private JTextArea textArea = new JTextArea();

  public MindWaveRawPanel()
  {
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    MindWaveContext.getInstance().addListener(new MindWaveListener()
    {
      @Override
      public void parsing(byte[] ba)
      {
        String str = "";
        for (int i=0; i<ba.length; i++)
          str += (DumpUtil.lpad(Integer.toHexString(ba[i] & 0xFF).toUpperCase(), 2, "0") + " ");
        textArea.setText(textArea.getText() + "\n" + str);
        textArea.setCaretPosition(textArea.getDocument().getLength());
      }
    });
  }

  private void jbInit()
    throws Exception
  {
    this.setLayout(borderLayout1);
    this.setSize(new Dimension(920, 415));
    this.setPreferredSize(new Dimension(70, 100));
    this.setMinimumSize(new Dimension(70, 70));

    textArea.setFont(new Font("Source Code Pro", 0, 11));
    textArea.setForeground(Color.green);
    textArea.setBackground(SystemColor.windowText);
    jScrollPane.getViewport().add(textArea, null);
    this.add(jScrollPane, BorderLayout.CENTER);
  }

  public void paintComponent(Graphics gr)
  {
//  centerPanel.repaint();
  }
}
