import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    public WeatherAppGui() {
        //set gui and add a title
        super("Weather App");
        //configure gui to end the program's process once it has been close
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //Set the size of gui (pixels9
        setSize(450,650);
        //load gui at the center
        setLocationRelativeTo(null);
        //make layout manager null to manually position components within the gui
        setLayout(null);
        //prevent any resize
        setResizable(false);

        addGuiComponents();
    }
    private void addGuiComponents(){
        //search field
        JTextField searchTextField = new JTextField();
        //set the location and size of components
        searchTextField.setBounds(15,15,351,45);
        //change font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN,24));

        add(searchTextField);

        //search button
        JButton searchButton = new JButton(loadImage("assets/search.png"));
    }
    //Use to craete images in gui components
    private ImageIcon loadImage(String resurcePath){
        try{
            //read the immage file from the path given
            BufferedImage image = ImageIO.read(new File(resurcePath));
            //returns an image icon so that component can raed it
            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Couldn't find the image");
        return null;
    }
}
