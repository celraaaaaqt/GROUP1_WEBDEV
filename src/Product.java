
public class Product {
    
    private int id;
    private String name;
    private String price;
    private String Description;
    private byte [] picture;
    private int stock;
    public Product(int pid, String pname, String pprice, String Desc, byte [] pimg, int stock )
    {
        this.id = pid;
        this.name = pname;
        this.price = pprice;
        this.Description = Desc;
        this.picture = pimg;
        this.stock = stock;
    }
    public int getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
           
    }
    public String getPrice()
    {
        return price; 
    }
    public String getDescription()
    {
        return Description;
    }
    public byte [] getImage()
    {
        return picture;
    }
    public int getStock()
    {
        return stock;
    }
}