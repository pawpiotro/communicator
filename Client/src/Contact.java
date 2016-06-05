/**
 * Klasa, której obiekty przechowują informacje o kontaktach z listy.
 * Na razie jest to tylko ID i nazwa.
 */
public class Contact {
    private int id;
    private String name;

    public Contact(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getid() {
        return id;
    }

    public String getName() {
        return name;
    }
}
