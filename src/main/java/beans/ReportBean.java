package beans;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import localization.LocalizationManager;
import model.VehicleType;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import dao.ApplicationDAO;


@Named("reportBean")
@RequestScoped
public class ReportBean {

    @Inject
    private ApplicationDAO applicationDAO;
    private int option;
    private int indicator;
    private PieChartModel pieChartModel = new PieChartModel();
    private CartesianChartModel barChartModel1 = new CartesianChartModel();
    private CartesianChartModel barChartModel2 = new CartesianChartModel();


    public ReportBean() {
    }

    @PostConstruct
    private void init() {
        option = 1;
        indicator = 0;

        Map<String, String> params = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap();
        String reportId = params.get("reportId");

        if (reportId == null) {
            reportId = "1";
        }

        if (reportId.equals("1")) {
            showReportCarsByOptions();
        } else if (reportId.equals("2")) {
            showReportCarsByFeatures();
        }
    }


    //getters
    public int getOption() {
        return option;
    }

    public int getIndicator() {
        return indicator;
    }

    public PieChartModel getPieChartModel() {
        return pieChartModel;
    }

    public CartesianChartModel getBarChartModel1() {
        return barChartModel1;
    }

    public CartesianChartModel getBarChartModel2() {
        return barChartModel2;
    }


    //setters
    public void setOption(int option) {
        this.option = option;
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public void setPieChartModel(PieChartModel pieChartModel) {
        this.pieChartModel = pieChartModel;
    }

    public void setBarChartModel1(CartesianChartModel barChartModel) {
        this.barChartModel1 = barChartModel;
    }

    public void setBarChartModel2(CartesianChartModel barChartModel) {
        this.barChartModel2 = barChartModel;
    }


    //methods
    private String getObjectAppearance(Object o) {
        switch (option) {
            case 1:
                return o.toString();
            case 2:
                return ((VehicleType) o).getName();
            case 3:
                short gearbox = (short) o;
                switch (gearbox) {
                    case 1:
                        return LocalizationManager.getLocalizedText("gearboxManual");
                    case 2:
                        return LocalizationManager.getLocalizedText("gearboxAutomatic");
                }
            case 4:
                short drive = (short) o;
                switch (drive) {
                    case 1:
                        return LocalizationManager.getLocalizedText("frontWheelDrive");
                    case 2:
                        return LocalizationManager.getLocalizedText("rearWheelDrive");
                    case 3:
                        return LocalizationManager.getLocalizedText("fullWheelDrive");
                }
        }
        return null;
    }

    public void showReportCarsByOptions() {
        pieChartModel.clear();
        List<Object[]> result = applicationDAO.getIndicatorsByOptions(Integer.valueOf(option));
        for (Object[] objects : result) {
            pieChartModel.set(getObjectAppearance(objects[2]),
                    (Number) objects[indicator]);
        }
    }

    private String getFeatureByIndex(int index) {
        switch (index) {
            case 5:
                return "a/c";
            case 6:
                return "cruise control";
            case 7:
                return "cabriolet";
            default:
                return null;
        }
    }

    public void showReportCarsByFeatures() {
        barChartModel1.clear();
        barChartModel2.clear();
        String with = LocalizationManager.getLocalizedText("booleanTrue");
        String without = LocalizationManager.getLocalizedText("booleanFalse");
        ChartSeries withQ = new ChartSeries(with);
        ChartSeries withoutQ = new ChartSeries(without);
        ChartSeries withA = new ChartSeries(with);
        ChartSeries withoutA = new ChartSeries(without);

        for (int i = 5; i < 8; i++) {
            List<Object[]> result = applicationDAO.getIndicatorsByOptions(i);
            withQ.set(getFeatureByIndex(i), 0);
            withoutQ.set(getFeatureByIndex(i), 0);
            withA.set(getFeatureByIndex(i), 0);
            withoutA.set(getFeatureByIndex(i), 0);
            for (Object[] objects : result) {
                if ((boolean) objects[2]) {
                    withQ.set(getFeatureByIndex(i), (Number) objects[0]);
                    withA.set(getFeatureByIndex(i), (Number) objects[1]);
                } else {
                    withoutQ.set(getFeatureByIndex(i), (Number) objects[0]);
                    withoutA.set(getFeatureByIndex(i), (Number) objects[1]);
                }
            }
        }

        barChartModel1.addSeries(withQ);
        barChartModel1.addSeries(withoutQ);
        barChartModel2.addSeries(withA);
        barChartModel2.addSeries(withoutA);

    }

}
