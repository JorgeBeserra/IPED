package dpf.sp.gpinf.indexer.desktop.api;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import iped.viewers.api.ResultSetViewer;
import iped.viewers.api.ResultSetViewerConfiguration;

public class FixedResultSetViewerConfiguration implements ResultSetViewerConfiguration {

    List<ResultSetViewer> viewers = new ArrayList<ResultSetViewer>();

    @Override
    public List<ResultSetViewer> getResultSetViewers() {
        try {
            if (viewers.size() == 0) {
                Class<?> mapaClass = Class.forName("iped.geo.MapaViewer");
                ResultSetViewer mapa = (ResultSetViewer) mapaClass.getDeclaredConstructor().newInstance();
                viewers.add(mapa);
            }
            return viewers;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            /* retorna lista vazia */
            return new ArrayList<ResultSetViewer>();
        }
    }

}
