package fr.dorvak.workmc.utils.images;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import fr.dorvak.workmc.WorkMc;
import fr.dorvak.workmc.utils.Constants;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Dorvak
 *
 * Licensed with MIT - Author must be mentionned
 */

public class LevelImage {
	
	private User user;
	private Member member;
	private int level, totalExp, requiredExp;
	private WorkMc main;

	public LevelImage(User user) {
		this.user = user;
		this.main = WorkMc.getInstance();
		this.member = main.getJDA().getGuildById(Constants.GUILD_ID).getMember(user);
		this.level = main.getLevelManager().getLevel(user.getId());
		this.totalExp = main.getLevelManager().getExp(user.getId());
		this.requiredExp = main.getLevelManager().calculateExpForNextLevel(level);
	}

	public File get() {
		int width = 500;
        int height = 200;
 
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        Rectangle rect = new Rectangle(width, height);
        
        URL logoUrl = null;
        BufferedImage background = null;
        BufferedImage logo = null;
        Font roboto = null;
        
		try {
			roboto = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("Roboto-Regular.ttf"));
			background = ImageIO.read(getClass().getResourceAsStream("level_background.jpg"));
			logoUrl = new URL(user.getAvatarUrl().replace(".gif", ".png"));
			URLConnection uc = logoUrl.openConnection();
			uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			uc.connect();
			uc.getInputStream();
			logo = ImageIO.read(uc.getInputStream());
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
        
        g2d.drawImage(background, 0, 0, null);
        
        Font f = roboto.deriveFont(25.0F);
        FontMetrics metrics = g2d.getFontMetrics(f);
        
        int x = rect.x + (rect.width - metrics.stringWidth(member.getEffectiveName() + "#" + member.getUser().getDiscriminator())) / 2;
        
        g2d.setFont(f);
        
        g2d.setColor(new Color(236, 240, 241));
        g2d.drawString(member.getEffectiveName() + "#" + member.getUser().getDiscriminator(), x, height - 50);
        
        g2d.setColor(new Color(52, 152, 219, 100));
        g2d.fillRoundRect(20, height - 40, width - 40, 30, 20, 20);
        g2d.setColor(new Color(41, 128, 185));
        g2d.fillRoundRect(20, height - 40, (width - 40) * totalExp / requiredExp, 30, 20, 20);
        g2d.drawRoundRect(20, height - 40, width - 40, 30, 20, 20);
        
        
        BufferedImage output = new BufferedImage(110, 110, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, 110, 110, 110, 110));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(logo, 0, 0, null);
        
        g2.dispose();
        try {
			ImageIO.write(output, "png", new File("logo_round.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        g2d.drawImage(output, width/2 - 55, 10, null);
        
        f = roboto.deriveFont(20.0F);
        metrics = g2d.getFontMetrics(f);
        x = rect.x + (rect.width - metrics.stringWidth("Niveau " + level + " - " + totalExp + "/" + requiredExp)) / 2;
        
        g2d.setFont(f);
        
        g2d.setColor(Color.BLACK);
        g2d.drawString("Niveau " + level + " - " + totalExp + "/" + requiredExp, x, height - 18);
        
        g2d.dispose();
        File file = new File("level.png");
        try {
			ImageIO.write(bufferedImage, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return file;
	}
}
