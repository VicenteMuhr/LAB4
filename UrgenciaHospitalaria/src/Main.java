import java.io.*;
import java.util.*;

class Paciente{
    String nombre;
    String apellido;
    String id;
    int categoria;
    long tiempoLlegada;
    String estado;
    String area;
    Stack<String> historialCambios;

    public Paciente(String nombre, String apellido, String id, int categoria,
                    long tiempoLlegada, String estado, String area) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.id = id;
        this.categoria = categoria;
        this.tiempoLlegada = tiempoLlegada;
        this.estado = estado;
        this.area = area;
        this.historialCambios = new Stack<>();
        registrarCambio("Paciente en categoria " + categoria + " y estado " + estado);
    }

    public String getNombre() {return nombre;}
    public String getApellido() {return apellido;}
    public String getId() {return id;}
    public int getCategoria() {return categoria;}
    public long getTiempoLlegada() {return tiempoLlegada;}
    public String getEstado() {return estado;}
    public String getArea() {return area;}

    public void setEstado(String estado) {this.estado = estado;
        registrarCambio("Estado cambiado a " + estado);}
    public void setCategoria(int nuevaCategoria){
        registrarCambio("Categoria cambiado de " +
                this.categoria + " a " + nuevaCategoria);
        this.categoria = nuevaCategoria;}
    public void setArea(String area){
        registrarCambio("Área cambiada de " + this.area + " a " + area);
        this.area = area;
    }
    long tiempoEsperaActual(){
        return (System.currentTimeMillis()-tiempoLlegada)/60000;
    }
    void registrarCambio(String descripcion){historialCambios.push(descripcion);}
    String obtenerUltimoCambio(){
        if(!historialCambios.isEmpty()){
            return historialCambios.pop();
        }
        return null;
    }
    public Stack<String> getHistorialCambios(){
        return historialCambios;}
}
class AreaAtencion{
    String NOMBRE;
    PriorityQueue<Paciente> pacientesHeap;
    int capacidadMaxima;

    public AreaAtencion(String nombre, int capacidadMaxima){
        this.NOMBRE = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.pacientesHeap = new PriorityQueue<>(new Comparator<Paciente>() {
            public int compare(Paciente p1, Paciente p2){
                if(p1.getCategoria() != p2.getCategoria()){
                    return Integer.compare(p1.getCategoria(), p2.getCategoria());
                }
                return Long.compare(p1.getTiempoLlegada(), p2.getTiempoLlegada());
            }
        });
    }
    public String getNOMBRE() {return NOMBRE;}
    public int getCapacidadMaxima() {return capacidadMaxima;}
    public int getCantidadPacientesHeap(){return pacientesHeap.size();}

    public void ingresarPaciente(Paciente p){
        if(!estaSaturada()){
            pacientesHeap.offer(p);
            p.setEstado("En area de atención");
        }else{
            System.out.println("[SATURACION] Área " + NOMBRE + " ya está saturada. No se pudo ingresar al paciente " + p.getId());
        }
    }
    public boolean estaSaturada(){return pacientesHeap.size() >= capacidadMaxima;}
    public Paciente atenderPaciente(){
        if(!pacientesHeap.isEmpty()){
            Paciente pacienteAtendido = pacientesHeap.poll();
            pacienteAtendido.setEstado("Atendido");
            return pacienteAtendido;
        }
        return null;
    }
    public List<Paciente> obtenerPacientesPorHeapSort(){
        List<Paciente> pacientesOrdenados = new ArrayList<>();
        PriorityQueue<Paciente> tempHeap = new PriorityQueue<>(pacientesHeap);
        while(!tempHeap.isEmpty()){
            pacientesOrdenados.add(tempHeap.poll());
        }
        return pacientesOrdenados;
    }
}
class Hospital{
    Map<String, Paciente> pacientesTotales;
    PriorityQueue<Paciente> colaAtencion;
    Map<String, AreaAtencion> areasAtencion;
    List<Paciente> pacientesAtentidos;
    Random random;

    Hospital(){
        this.pacientesTotales = new HashMap<>();
        this.colaAtencion = new PriorityQueue<>(new Comparator<Paciente>() {
            public int compare(Paciente p1, Paciente p2){
                if(p1.getCategoria() != p2.getCategoria()){
                    return Integer.compare(p1.getCategoria(), p2.getCategoria());
                }
                return Long.compare(p1.getTiempoLlegada(), p2.getTiempoLlegada());
            }
        });
        this.areasAtencion = new HashMap<>();
        this.pacientesAtentidos = new ArrayList<>();
        this.random = new Random();

        areasAtencion.put("SAPU", new AreaAtencion("SAPU", 50));
        areasAtencion.put("Urgencia Adulto",
                new AreaAtencion("Urgencia Adulto", 100));
        areasAtencion.put("Urgencia Infantil",
                new AreaAtencion("Urgencia Infantil", 70));
    }
    public void registrarPaciente(Paciente p){
        pacientesTotales.put(p.getId(), p);
        colaAtencion.offer(p);

        String[] nombresAreas = {"SAPU", "Urgencia Infantil", "Urgencia Adulto"};
        p.setArea(nombresAreas[random.nextInt(nombresAreas.length)]);
    }
    public void reasignarCategoria(String id, int nuevaCategoria){
        Paciente p = pacientesTotales.get(id);
        if(p != null){
            p.setCategoria(nuevaCategoria);
            colaAtencion.remove(p);
            colaAtencion.offer(p);
        }
    }
    public Paciente atenderSiguiente(){
        Paciente pacienteAtendido = colaAtencion.poll();
        if(pacienteAtendido != null){
            AreaAtencion area = areasAtencion.get(pacienteAtendido.getArea());
            if(area != null){
                area.ingresarPaciente(pacienteAtendido);
            }
        }
        return pacienteAtendido;
    }
    public List<Paciente> obtenerPacientesPorCategoria(int categoria){
        List<Paciente> pacientesPorCat = new ArrayList<>();
        for(Paciente p : colaAtencion){
            if(p.getCategoria() == categoria){
                pacientesPorCat.add(p);
            }
        }
        return pacientesPorCat;
    }
    public AreaAtencion obtenerAreaAtencion(String NOMBRE){
        return areasAtencion.get(NOMBRE);
    }
    public Map<String, Paciente> getPacientesTotales(){return pacientesTotales;}
    public PriorityQueue<Paciente> getColaAtencion(){return colaAtencion;}
    public List<Paciente> getPacientesAtendidos(){return pacientesAtentidos;}
}
class GeneradorPacientes{
    private static final String[] NOMBRES ={"Rayo", "Marge",
            "Lilo", "Juana", "Luis", "Laura", "Juan", "Sofia"};
    private static final String[] APELLIDOS ={"Riquelme", "Papaleta",
            "Mcqueen", "Jesucristo", "Stich", "delArco", "ElCaballo"};
    private static final Random RANDOM = new Random();

    public List<Paciente> generarPacientes(int n, long timestampInicio){
        List<Paciente> pacientes = new ArrayList<>();
        for(int i = 0; i < n; i++){
            String nombre = NOMBRES[RANDOM.nextInt(NOMBRES.length)];
            String apellido = APELLIDOS[RANDOM.nextInt(APELLIDOS.length)];
            String id = "ID-" + (i + 1);
            int categoria = generarCategoriaAleatoria();
            long tiempoLlegada = timestampInicio + (long) i * 10 * 60 * 1000;

            Paciente p = new Paciente(nombre, apellido, id, categoria,
                    tiempoLlegada, "En_espera", null);
            pacientes.add(p);
        }
        return pacientes;
    }
    public int generarCategoriaAleatoria(){
        double prob = RANDOM.nextDouble();
        if(prob < 0.10) return 1;
        else if(prob < 0.10 + 0.15) return 2;
        else if(prob < 0.10 + 0.15 + 0.18) return 3;
        else if(prob < 0.10 + 0.15 + 0.18 + 0.27) return 4;
        else return 5;
    }
    public void guardarPacientesEnArchivo(List<Paciente> pacientes, String fileName){
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Paciente p : pacientes) {
                writer.println(p.getId() + "," + p.getNombre() + "," +
                        p.getApellido() + "," +
                        p.getCategoria() + "," + p.getTiempoLlegada() + "," +
                        p.getEstado() + "," + p.getArea());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SimuladorUrgencia {

    Hospital hospital;
    List<Paciente> pacientesGenerados;
    Map<Integer, Long> tiempoMaxEspera;

    Map<Integer, Integer> pacientesAtendidosPorCategoria;
    Map<Integer, Long> tiempoTotalEsperaPorCategoria;
    List<Paciente> pacientesExcedieronTiempoMax;
    Map<Integer, Long> peorTiempoEsperaPorCategoria;
    long tiempoActualSimulacionMilis;

    public SimuladorUrgencia(List<Paciente> pacientesGenerados) {
        this.hospital = new Hospital();
        this.pacientesGenerados = pacientesGenerados;
        this.tiempoMaxEspera = new HashMap<>();
        tiempoMaxEspera.put(1, 0L);
        tiempoMaxEspera.put(2, 30L);
        tiempoMaxEspera.put(3, 90L);
        tiempoMaxEspera.put(4, 180L);
        tiempoMaxEspera.put(5, Long.MAX_VALUE);

        this.pacientesAtendidosPorCategoria = new HashMap<>();
        this.tiempoTotalEsperaPorCategoria = new HashMap<>();
        this.pacientesExcedieronTiempoMax = new ArrayList<>();
        this.peorTiempoEsperaPorCategoria = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            pacientesAtendidosPorCategoria.put(i, 0);
            tiempoTotalEsperaPorCategoria.put(i, 0L);
            peorTiempoEsperaPorCategoria.put(i, 0L);
        }
        this.tiempoActualSimulacionMilis = pacientesGenerados.isEmpty() ?
                System.currentTimeMillis() : pacientesGenerados.get(0).getTiempoLlegada();
    }

    public void simular(int pacientesPorDia) {
        long duracionSimulacionMin = 24 * 60;
        int pacientesLlegadosIndex = 0;
        int ingresosAcumulados = 0;

        System.out.println("--- INICIANDO SIMULACIÓN DE URGENCIA ---");
        System.out.println("Duración: " + duracionSimulacionMin + " minutos simulados.");

        PriorityQueue<Paciente> colaGeneralHospital = hospital.getColaAtencion();

        for(long minutoActualSimulacion = 0; minutoActualSimulacion < duracionSimulacionMin; minutoActualSimulacion++){
            if(!pacientesGenerados.isEmpty()){
                this.tiempoActualSimulacionMilis = pacientesGenerados.get(0).getTiempoLlegada() +
                        (minutoActualSimulacion * 60 * 1000);
            }else{
                this.tiempoActualSimulacionMilis = System.currentTimeMillis() +
                        (minutoActualSimulacion * 60 * 1000);
            }
            while (pacientesLlegadosIndex < pacientesGenerados.size() &&
                    pacientesGenerados.get(pacientesLlegadosIndex).getTiempoLlegada() <= this.tiempoActualSimulacionMilis) {
                Paciente nuevoPaciente = pacientesGenerados.get(pacientesLlegadosIndex);
                hospital.registrarPaciente(nuevoPaciente);
                ingresosAcumulados++;
                pacientesLlegadosIndex++;
            }
            if (minutoActualSimulacion % 15 == 0) {
                atenderPaciente(this.tiempoActualSimulacionMilis, colaGeneralHospital);
            }
            if (ingresosAcumulados >= 3) {
                atenderPaciente(this.tiempoActualSimulacionMilis, colaGeneralHospital);
                atenderPaciente(this.tiempoActualSimulacionMilis, colaGeneralHospital);
                ingresosAcumulados = 0;
            }
            if (minutoActualSimulacion == 60) {
                Paciente pacienteAReasignar = hospital.getPacientesTotales().get("ID-5");
                if (pacienteAReasignar != null && pacienteAReasignar.getEstado().equalsIgnoreCase("En_espera")) {
                    System.out.println("[EVENTO ESPECÍFICO] Minuto " + minutoActualSimulacion + ": Reasignando categoría de " +
                            pacienteAReasignar.getId() + " a C1 (por prueba de reasignación).");
                    hospital.reasignarCategoria(pacienteAReasignar.getId(), 1);
                }
            }
            if(minutoActualSimulacion == 45){
                Paciente pParaCambio = hospital.getPacientesTotales().get("ID-3");
                if(pParaCambio != null && pParaCambio.getEstado().equalsIgnoreCase("En_espera")){
                    int oldCat = pParaCambio.getCategoria();
                    int newCat = 1;
                    System.out.printf("[EVENTO ESPECÍFICO] Minuto %d: Reasignando categoría de %s de C%d a C%d (por prueba de reasignación).\n",
                            minutoActualSimulacion, pParaCambio.getId(), oldCat, newCat);
                    hospital.reasignarCategoria(pParaCambio.getId(), newCat);
                } else {
                    System.out.println("[INFO] Minuto " + minutoActualSimulacion +
                            ": Paciente ID-3 no encontrado o ya atendido, no se pudo reasignar (esto es normal si ya fue atendido).");
                }
            }
        }
        System.out.println("\n--- SIMULACIÓN FINALIZADA ---");
        imprimirResultadosSimulacion();
    }
    public void atenderPaciente(long tiempoActualMilis, PriorityQueue<Paciente> colaGeneralHospital) {
        if (colaGeneralHospital.isEmpty()) {
            return;
        }
        Paciente pacienteAAtender = null;
        for (Paciente p : new ArrayList<>(colaGeneralHospital)) {
            long tiempoEsperaRealPacienteMilis = tiempoActualMilis - p.getTiempoLlegada();
            long tiempoMaximoMilis = tiempoMaxEspera.get(p.getCategoria()) * 60 * 1000;

            if (tiempoMaximoMilis != Long.MAX_VALUE && tiempoEsperaRealPacienteMilis >= tiempoMaximoMilis) {
                if (pacienteAAtender == null || p.getCategoria() < pacienteAAtender.getCategoria() ||
                        (p.getCategoria() == pacienteAAtender.getCategoria() && p.getTiempoLlegada() < pacienteAAtender.getTiempoLlegada())) {
                    pacienteAAtender = p;
                }
            }
        }
        if (pacienteAAtender != null) {
            colaGeneralHospital.remove(pacienteAAtender);
            if (!pacientesExcedieronTiempoMax.contains(pacienteAAtender)) {
                pacientesExcedieronTiempoMax.add(pacienteAAtender);
            }
        } else {
            if (!colaGeneralHospital.isEmpty()) {
                pacienteAAtender = colaGeneralHospital.poll();
            }
        }

        if (pacienteAAtender != null) {
            AreaAtencion areaDesignada = hospital.obtenerAreaAtencion(pacienteAAtender.getArea());
            if (areaDesignada != null) {
                areaDesignada.ingresarPaciente(pacienteAAtender);
            }

            pacienteAAtender.setEstado("Atendido");
            hospital.getPacientesAtendidos().add(pacienteAAtender);

            long esperaReporteMin = (tiempoActualMilis - pacienteAAtender.getTiempoLlegada()) / (60 * 1000);

            int categoria = pacienteAAtender.getCategoria();
            pacientesAtendidosPorCategoria.put(categoria, pacientesAtendidosPorCategoria.getOrDefault(categoria, 0) + 1);
            tiempoTotalEsperaPorCategoria.put(categoria, tiempoTotalEsperaPorCategoria.getOrDefault(categoria, 0L) + esperaReporteMin);
            if (esperaReporteMin > peorTiempoEsperaPorCategoria.getOrDefault(categoria, 0L)) {
                peorTiempoEsperaPorCategoria.put(categoria, esperaReporteMin);
            }
        }
    }
    public Map<Integer, Integer> getPacientesAtendidosPorCategoria() {
        return pacientesAtendidosPorCategoria;
    }
    public Map<Integer, Double> getPromedioTiempoEsperaPorCategoria() {
        Map<Integer, Double> promedios = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            int totalAtendidos = pacientesAtendidosPorCategoria.getOrDefault(i, 0);
            long tiempoTotal = tiempoTotalEsperaPorCategoria.getOrDefault(i, 0L);
            if (totalAtendidos > 0) {
                promedios.put(i, (double) tiempoTotal / totalAtendidos);
            } else {
                promedios.put(i, 0.0);
            }
        }
        return promedios;
    }
    public List<Paciente> getPacientesExcedieronTiempoMaximo() {
        return pacientesExcedieronTiempoMax;}
    public Map<Integer, Long> getPeorTiempoEsperaPorCategoria() {
        return peorTiempoEsperaPorCategoria;}
    public int getTotalPacientesAtendidos() {
        return hospital.getPacientesAtendidos().size();}
    public Hospital getHospital() {
        return hospital;}

    public void imprimirResultadosSimulacion() {
        System.out.println("\n--- REPORTE FINAL DE SIMULACIÓN ---");
        System.out.println("Total de pacientes atendidos: " + getTotalPacientesAtendidos());

        System.out.println("\nPacientes atendidos por categoría:");
        getPacientesAtendidosPorCategoria().forEach((cat, count) ->
                System.out.println("  C" + cat + ": " + count)
        );

        System.out.println("\nTiempo promedio de espera por categoría (minutos):");
        getPromedioTiempoEsperaPorCategoria().forEach((cat, avg) ->
                System.out.printf("  C%d: %.2f minutos\n", cat, avg)
        );

        System.out.println("\nPeor tiempo de espera registrado por categoría (minutos):");
        getPeorTiempoEsperaPorCategoria().forEach((cat, worst) ->
                System.out.println("  C" + cat + ": " + worst + " minutos")
        );

        if (!pacientesExcedieronTiempoMax.isEmpty()) {
            System.out.println("\nPacientes que excedieron el tiempo máximo de espera:");
            for (Paciente p : pacientesExcedieronTiempoMax) {
                long esperaRealMin = (this.tiempoActualSimulacionMilis - p.getTiempoLlegada()) / (60 * 1000);
                String maxTimeStr = (tiempoMaxEspera.get(p.getCategoria()) == Long.MAX_VALUE) ? "N/A" : String.valueOf(tiempoMaxEspera.get(p.getCategoria()));

                System.out.printf("  - %s (C%d) - Tiempo límite: %s min, Tiempo real de espera: %d min\n",
                        p.getId(), p.getCategoria(), maxTimeStr, esperaRealMin);
            }
        } else {
            System.out.println("\nNingún paciente excedió el tiempo máximo de espera.");
        }

        System.out.println("\nPacientes restantes en cola de espera (al final de la simulación):");
        PriorityQueue<Paciente> tempCola = new PriorityQueue<>(hospital.getColaAtencion());
        if (tempCola.isEmpty()) {
            System.out.println("  La cola de espera está vacía.");
        } else {
            while (!tempCola.isEmpty()) {
                Paciente p = tempCola.poll();
                long esperaActualMin = (this.tiempoActualSimulacionMilis - p.getTiempoLlegada()) / (60 * 1000);
                System.out.printf("  - %s (C%d) - Esperando por %d minutos.\n", p.getId(), p.getCategoria(), esperaActualMin);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        GeneradorPacientes generador = new GeneradorPacientes();
        int numPacientesPara24Horas = (24 * 60) / 10;
        long timestampInicioSimulacion = System.currentTimeMillis();

        System.out.println("\n=============================================");

        System.out.println("\n--- PRUEBA 1: SIMULACIÓN BASE ---");
        List<Paciente> pacientesBase = generador.generarPacientes(numPacientesPara24Horas, timestampInicioSimulacion);
        generador.guardarPacientesEnArchivo(pacientesBase, "Pacientes_24h_Base.txt");
        SimuladorUrgencia simuladorBase = new SimuladorUrgencia(pacientesBase);
        simuladorBase.simular(numPacientesPara24Horas);

        Paciente pacienteID5 = simuladorBase.getHospital().getPacientesTotales().get("ID-5");
        if (pacienteID5 != null) {
            System.out.println("\n--- Historial de cambios para Paciente ID-5 (POST-SIMULACIÓN PRUEBA 1) ---");
            System.out.println("Estado final de ID-5: " + pacienteID5.getEstado());
            Stack<String> historialID5 = pacienteID5.getHistorialCambios();
            Stack<String> tempHistorialID5 = new Stack<>();
            while(!historialID5.isEmpty()) {
                tempHistorialID5.push(historialID5.pop());
            }
            while(!tempHistorialID5.isEmpty()) {
                System.out.println("  - " + tempHistorialID5.pop());
            }
        }
        System.out.println("\n--- PRUEBA 2: CÁLCULO DE PROMEDIO POR CATEGORÍA (15 EJECUCIONES) ---");
        List<Map<Integer, Double>> promediosPorEjecucion = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            List<Paciente> pacientesParaPromedio = generador.generarPacientes(numPacientesPara24Horas,
                    timestampInicioSimulacion + (i * 24 * 60 * 60 * 1000L));
            SimuladorUrgencia simuladorTemp = new SimuladorUrgencia(pacientesParaPromedio);
            simuladorTemp.simular(numPacientesPara24Horas);
            promediosPorEjecucion.add(simuladorTemp.getPromedioTiempoEsperaPorCategoria());
        }
        Map<Integer, Double> promedioFinalPorCategoria = new HashMap<>();
        for (int cat = 1; cat <= 5; cat++) {
            double sumaPromedios = 0.0;
            int contadorEjecucionesConDatos = 0;
            for (Map<Integer, Double> promedios : promediosPorEjecucion) {
                if (promedios.containsKey(cat) && promedios.get(cat) != null) {
                    sumaPromedios += promedios.get(cat);
                    contadorEjecucionesConDatos++;
                }
            }
            if (contadorEjecucionesConDatos > 0) {
                promedioFinalPorCategoria.put(cat, sumaPromedios / contadorEjecucionesConDatos);
            } else {
                promedioFinalPorCategoria.put(cat, 0.0);
            }
        }
        System.out.println("\n--- RESULTADOS PROMEDIO DE ESPERA FINAL (15 EJECUCIONES DE PRUEBA 2) ---");
        promedioFinalPorCategoria.forEach((cat, avg) ->
                System.out.printf("  C%d: %.2f minutos\n", cat, avg)
        );
        System.out.println("\n--- PRUEBA 3: SATURACIÓN DEL SISTEMA ---");
        int numPacientesSaturacion = 300;
        List<Paciente> pacientesSaturacion = generador.generarPacientes(numPacientesSaturacion, timestampInicioSimulacion);
        SimuladorUrgencia simuladorSaturacion = new SimuladorUrgencia(pacientesSaturacion);
        simuladorSaturacion.simular(numPacientesSaturacion);

        System.out.println("\n--- PRUEBA 4: CAMBIO DE CATEGORÍA ESPECÍFICO (PACIENTE ID-3) ---");
        List<Paciente> pacientesCambioCat = generador.generarPacientes(50, timestampInicioSimulacion);
        SimuladorUrgencia simuladorCambioCat = new SimuladorUrgencia(pacientesCambioCat);
        simuladorCambioCat.simular(50);

        System.out.println("\n--- Historial de cambios para Paciente ID-3 (POST-SIMULACIÓN PRUEBA 4) ---");
        Paciente pFinalCambio = simuladorCambioCat.getHospital().getPacientesTotales().get("ID-3");
        if (pFinalCambio != null) {
            System.out.println("Estado final de ID-3: " + pFinalCambio.getEstado());
            Stack<String> historialID3 = pFinalCambio.getHistorialCambios();
            Stack<String> tempHistorialID3 = new Stack<>();
            while(!historialID3.isEmpty()) {
                tempHistorialID3.push(historialID3.pop());
            }
            while(!tempHistorialID3.isEmpty()) {
                System.out.println("  - " + tempHistorialID3.pop());
            }
        } else {
            System.out.println("Paciente ID-3 no fue encontrado o no fue generado en esta simulación específica.");
        }
        System.out.println("\n=============================================");
    }
}
