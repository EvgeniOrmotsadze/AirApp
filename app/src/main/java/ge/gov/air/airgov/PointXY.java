package ge.gov.air.airgov;

public class PointXY {

    private double val;
    private String date;

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PointXY{" +
                "val='" + val + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
