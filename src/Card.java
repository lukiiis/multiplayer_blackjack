public class Card {

    public int value;
    public String color;
    public String face;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public Card(int value, String color) {
        this.value = value;
        this.color = color;
        face = null;
    }

    public Card(int value, String color, String face) {
        this.value = value;
        this.color = color;
        this.face = face;
    }

    @Override
    public String toString(){
        if(this.face != null)
            return face + " " + color + " | ";
        else
            return Integer.toString(this.value) + " " + color + " | ";
    }
}
