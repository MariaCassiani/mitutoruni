import java.io.*; // Importa clases para entrada/salida de archivos
import java.util.*; // Importa utilidades como Scanner, ArrayList, HashSet
import java.util.regex.*; // Importa utilidades para trabajar con expresiones regulares

public class MiTutor {

    // Clase interna que representa un usuario
    static class Usuario {
        String correo;
        String contrasena;

        Usuario(String correo, String contrasena) {
            this.correo = correo;
            this.contrasena = contrasena;
        }
    }

    // Clase interna que representa una tutoría
    static class Tutoria {
        String area;
        String docente;
        String fecha;
        String horaInicio;
        String horaFin;

        Tutoria(String area, String docente, String fecha, String horaInicio, String horaFin) {
            this.area = area;
            this.docente = docente;
            this.fecha = fecha;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }
    }

    // Lista de usuarios registrados
    static ArrayList<Usuario> usuarios = new ArrayList<>();
    // Lista de tutorías disponibles
    static ArrayList<Tutoria> tutorias = new ArrayList<>();

    // Nombres de los archivos para persistencia
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    static final String ARCHIVO_RESERVAS = "reservas.txt";

    // Scanner para leer entradas del usuario
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        cargarUsuariosDesdeArchivo(); // Carga los usuarios desde archivo
        cargarTutorias(); // Carga las tutorías disponibles (predefinidas)

        boolean salir = false;

        // Menú principal
        while (!salir) {
            System.out.println("\n--- MENÚ ---");
            System.out.println("1. Registrarse");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer del scanner

            switch (opcion) {
                case 1:
                    registrarUsuario(); // Registro
                    break;
                case 2:
                    iniciarSesion(); // Inicio de sesión
                    break;
                case 3:
                    salir = true; // Salir del sistema
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida."); // Validación
            }
        }
    }

    // Método para registrar un nuevo usuario
    public static void registrarUsuario() {
        System.out.print("Ingrese su correo electrónico: ");
        String correo = scanner.nextLine();

        if (!correoValido(correo)) {
            System.out.println("Correo no válido.");
            return;
        }

        // Verifica si el correo ya está registrado
        for (Usuario u : usuarios) {
            if (u.correo.equalsIgnoreCase(correo)) {
                System.out.println("Ese correo ya está registrado.");
                return;
            }
        }

        System.out.print("Ingrese su contraseña (al menos una mayúscula y un número): ");
        String contrasena = scanner.nextLine();

        if (!contrasenaValida(contrasena)) {
            System.out.println("La contraseña no cumple con los requisitos.");
            return;
        }

        // Agrega el nuevo usuario a la lista y al archivo
        Usuario nuevo = new Usuario(correo, contrasena);
        usuarios.add(nuevo);
        guardarUsuarioEnArchivo(nuevo);

        System.out.println("Registro exitoso.");
        mostrarMenuTutorias(nuevo.correo); // Muestra tutorías tras el registro
    }

    // Método para iniciar sesión
    public static void iniciarSesion() {
        System.out.print("Correo electrónico: ");
        String correo = scanner.nextLine();

        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();

        for (Usuario u : usuarios) {
            if (u.correo.equalsIgnoreCase(correo) && u.contrasena.equals(contrasena)) {
                System.out.println("¡Inicio de sesión exitoso!");
                mostrarMenuTutorias(correo); // Muestra menú de tutorías
                return;
            }
        }

        System.out.println("Correo o contraseña incorrectos.");

        // Si el correo no existe, ofrece registrarse
        boolean correoExiste = usuarios.stream().anyMatch(u -> u.correo.equalsIgnoreCase(correo));

        if (!correoExiste) {
            System.out.print("¿Deseas registrarte con este correo? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            if (respuesta.equals("s")) {
                registrarUsuarioConCorreo(correo);
            } else {
                System.out.println("Operación cancelada.");
            }
        }
    }

    // Registra un usuario usando un correo dado
    public static void registrarUsuarioConCorreo(String correo) {
        if (!correoValido(correo)) {
            System.out.println("Correo no válido.");
            return;
        }

        System.out.print("Ingrese una nueva contraseña (al menos una mayúscula y un número): ");
        String contrasena = scanner.nextLine();

        if (!contrasenaValida(contrasena)) {
            System.out.println("La contraseña no cumple con los requisitos.");
            return;
        }

        Usuario nuevo = new Usuario(correo, contrasena);
        usuarios.add(nuevo);
        guardarUsuarioEnArchivo(nuevo);

        System.out.println("Usuario registrado exitosamente con el correo proporcionado.");
        mostrarMenuTutorias(correo); // Va a menú de tutorías
    }

    // Guarda un usuario en el archivo usuarios.txt
    public static void guardarUsuarioEnArchivo(Usuario u) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            bw.write(u.correo + "," + u.contrasena);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        }
    }

    // Carga los usuarios desde el archivo
    public static void cargarUsuariosDesdeArchivo() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",", 2);
                if (partes.length == 2) {
                    usuarios.add(new Usuario(partes[0], partes[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
    }

    // Carga tutorías predeterminadas
    public static void cargarTutorias() {
        tutorias.add(new Tutoria("Redes de computo ", "Prof. Beatriz Alfaro", "2025-05-20", "10:00AM", "2:00PM"));
        tutorias.add(new Tutoria("Desarrollo de software ", "Prof. Cristian Cuadrado ", "2025-06-22", "8:00AM", "10:30AM"));
        tutorias.add(new Tutoria("Fundamentos de sistemas de información", "Prof. Xibia Hurtado", "2025-06-23", "12:00PM", "1:00PM"));
        tutorias.add(new Tutoria("Algebra líneal", "Prof. Amaury ", "2025-06-24", "9:00AM", "10:00AM"));
    }

    // Muestra las áreas de tutoría disponibles
    public static void mostrarMenuTutorias(String correoUsuario) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n--- ÁREAS DE TUTORÍA ---");
            Set<String> areas = new HashSet<>();
            for (Tutoria t : tutorias) {
                areas.add(t.area); // Extrae áreas únicas
            }

            List<String> listaAreas = new ArrayList<>(areas);
            for (int i = 0; i < listaAreas.size(); i++) {
                System.out.println((i + 1) + ". " + listaAreas.get(i));
            }
            System.out.println((listaAreas.size() + 1) + ". Cerrar sesión");

            System.out.print("Elige un área para ver tutorías: ");
            String entrada = scanner.nextLine();

            int eleccion;
            try {
                eleccion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
                continue;
            }

            if (eleccion == listaAreas.size() + 1) {
                System.out.println("Sesión finalizada.");
                salir = true;
            } else if (eleccion >= 1 && eleccion <= listaAreas.size()) {
                String areaElegida = listaAreas.get(eleccion - 1);
                mostrarTutoriasPorArea(areaElegida, correoUsuario);
            } else {
                System.out.println("Opción no válida.");
            }
        }
    }

    // Muestra las tutorías de un área específica y permite reservar
    public static void mostrarTutoriasPorArea(String area, String correoUsuario) {
        System.out.println("\n--- Tutorías de " + area + " ---");
        List<Tutoria> disponibles = new ArrayList<>();
        int index = 1;

        for (Tutoria t : tutorias) {
            if (t.area.equalsIgnoreCase(area)) {
                System.out.println(index + ". Docente: " + t.docente);
                System.out.println("   Fecha: " + t.fecha);
                System.out.println("   Hora: " + t.horaInicio + " - " + t.horaFin);
                disponibles.add(t);
                index++;
            }
        }

        if (disponibles.isEmpty()) {
            System.out.println("No hay tutorías disponibles.");
            return;
        }

        System.out.print("¿Deseas reservar una tutoría? (s/n): ");
        String respuesta = scanner.nextLine().toLowerCase();

        if (respuesta.equals("s")) {
            System.out.print("Elige el número de la tutoría que deseas reservar: ");
            try {
                int seleccion = Integer.parseInt(scanner.nextLine());
                if (seleccion >= 1 && seleccion <= disponibles.size()) {
                    Tutoria t = disponibles.get(seleccion - 1);
                    guardarReserva(correoUsuario, t);
                    System.out.println("¡Reserva realizada con éxito! Que tenga un lindo día.");
                } else {
                    System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
        }
    }

    // Guarda la reserva en el archivo
    public static void guardarReserva(String correo, Tutoria t) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_RESERVAS, true))) {
            bw.write(correo + " | " + t.area + " | " + t.docente + " | " + t.fecha + " | " + t.horaInicio + " - " + t.horaFin);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al guardar reserva: " + e.getMessage());
        }
    }

    // Valida el formato del correo con expresión regular
    public static boolean correoValido(String correo) {
        String regex = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";
        return Pattern.matches(regex, correo);
    }

    // Verifica que la contraseña tenga al menos una mayúscula y un número
    public static boolean contrasenaValida(String contrasena) {
        return contrasena.matches(".*[A-Z].*") && contrasena.matches(".*\\d.*");
    }
}
