package ru.ifmo.se.client;

import ru.ifmo.se.commands.*;
import ru.ifmo.se.model.User;
import ru.ifmo.se.musicians.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Gurin Minu
 * @version 0
 */
public class Reader {
    private File scriptName;
    private Integer argument;

    /**
     * Обрабатывает данные введенные пользователкм и возвращает команду
     *
     * @param in Scanner
     * @return CommandName
     */
    public ClassCommand readCommand(Scanner in) throws FileNotFoundException {
        String command = in.nextLine();
        String[] mas = command.split("\\s");
        boolean flag = true;
        CommandName commandName = null;
        ClassCommand classCommand;

        if (command.equals("")) {
            return commandFactory(CommandName.ERROR);
        }
        for (CommandName cn : CommandName.values()) {
            if (cn.getCommand().equals(mas[0].toLowerCase())) {
                commandName = cn;
                flag = false;
                break;
            }
        }
        if (flag) {
            return commandFactory(CommandName.ERROR);
        }
        if (mas.length == 1 && (commandName == CommandName.CLEAR || commandName == CommandName.EXIT || commandName == CommandName.HELP || commandName == CommandName.HISTORY || commandName == CommandName.INFO || commandName == CommandName.PRINT_DESCENDING || commandName == CommandName.SHOW || commandName == CommandName.MAX_BY_GENRE)) {
            return commandFactory(commandName);
        } else if (commandName == CommandName.ADD || commandName == CommandName.REMOVE_LOWER || commandName == CommandName.REMOVE_GREATER) {
            classCommand = commandFactory(commandName);
            classCommand.addBandInput(readCollectionObject(in));
            return classCommand;
        } else if (mas.length == 2) {
            if (commandName == CommandName.FILTER_LESS_THEN_NUMBER_OF_PARTICIPANTS || commandName == CommandName.REMOVE_BY_ID) {
                try {
                    argument = Integer.parseInt(mas[1]);
                    classCommand = commandFactory(commandName);
                    classCommand.setArgument(argument);
                    return classCommand;
                } catch (NumberFormatException e) {
                    return commandFactory(CommandName.ERROR);
                }
            } else if (commandName == CommandName.EXECUTE_SCRIPT) {
                Object object = readFile(mas[1]);
                if(object instanceof File){
                    scriptName = (File) object;
                }
                else {
                    System.out.println(object.toString());
                    return commandFactory(CommandName.ERROR);
                }
                Scanner scanner = new Scanner(scriptName);
                ArrayList<ClassCommand> classCommands = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    classCommands.add(readCommand(scanner));
                }
                classCommand = commandFactory(commandName);
                classCommand.setArgument(classCommands);
                return classCommand;
            } else if (commandName == CommandName.UPDATE) {
                try {
                    argument = Integer.parseInt(mas[1]);
                    classCommand = commandFactory(commandName);
                    classCommand.setArgument(argument);
                    classCommand.addBandInput(readCollectionObject(in));
                    return classCommand;
                } catch (NumberFormatException e) {
                    return commandFactory(CommandName.ERROR);
                }
            } else {
                return commandFactory(CommandName.ERROR);
            }
        } else {
            return commandFactory(CommandName.ERROR);
        }
    }

    /**
     * Возвращает введенные пользователем численные аргументы
     *
     * @return int
     */
    public int readArgument() {
        return argument;
    }

    /**
     * Возвращает файл скрипта, путь к которому ввел пользователь
     *
     * @return File
     */
    public File readScriptName() {
        return scriptName;
    }

    /**
     * Возвращает изначачальный файл, путь к которому указал пользователь
     *
     * @param in Scanner
     * @return File
     */
    public static File readFile(Scanner in) {
        System.out.println("Введите путь к файлу: ");
        String path = in.nextLine();
        Scanner file;
        boolean flag = true;
        File startFile = new File(path);
        while (flag) {
            try {
                if (Files.isHidden(startFile.toPath())) {
                    System.out.println("Файл спрятался, укажите другой или найдите его");
                    path = in.nextLine();
                    startFile = new File(path);
                    continue;
                } else if (!Files.isReadable(startFile.toPath())) {
                    System.out.println("Файл нельзя прочитать, укажите другой или измените разрешение");
                    path = in.nextLine();
                    startFile = new File(path);
                    continue;
                } else if (!Files.isWritable(startFile.toPath())) {
                    System.out.println("Файл нельзя изменить, укажите другой или измените разрешение");
                    path = in.nextLine();
                    startFile = new File(path);
                    continue;
                } else if (!Files.isExecutable(startFile.toPath())) {
                    System.out.println("Файл нельзя execute, укажите другой или измените разрешение");
                    path = in.nextLine();
                    startFile = new File(path);
                    continue;
                }
                file = new Scanner(startFile, "UTF-8");
                flag = false;
            } catch (NoSuchElementException | IOException | InvalidPathException e) {
                System.out.println("Неправильно введен путь повторите попытку: ");
                path = in.nextLine();
                startFile = new File(path);
            }
        }
        return startFile;
    }

    /**
     * Возвращает изначачальный файл, путь к которому указал пользователь
     *
     * @param path path
     * @return File
     */
    public static Object readFile(String path) {
        Scanner file;
        boolean flag = true;
        File startFile = new File(path);
        try {
            if (Files.isHidden(startFile.toPath())) {
                return("Файл спрятался, укажите другой или найдите его");
            } else if (!Files.isReadable(startFile.toPath())) {
                return ("Файл нельзя прочитать, укажите другой или измените разрешение");
            } else if (!Files.isWritable(startFile.toPath())) {
                return ("Файл нельзя изменить, укажите другой или измените разрешение");
            } else if (!Files.isExecutable(startFile.toPath())) {
                return ("Файл нельзя execute, укажите другой или измените разрешение");
            }
            file = new Scanner(startFile, "UTF-8");
            flag = false;
        } catch (NoSuchElementException | IOException | InvalidPathException e) {
            return ("Неправильно введен путь");
        }
        return startFile;
    }

    /**
     * Возвращает строку, введенную пользователем
     *
     * @param in Ввод пользователя
     * @return Ненулевая строка
     */
    String readString(Scanner in) {
        String string = in.nextLine();
        while (string.equals("")) {
            System.out.println("Некорректный ввод, попробуйте еще раз: ");
            string = in.nextLine();
        }
        return string;
    }

    /**
     * Возвращает long, введенный пользователем
     *
     * @param in Ввод пользователя
     * @return long
     */
    long readLong(Scanner in) {
        long x = 0;
        boolean flag = true;
        String s = in.nextLine();
        while (s.equals("") || flag || x == Long.MIN_VALUE || x == Long.MAX_VALUE) {
            flag = true;
            try {
                x = Long.parseLong(s);
                flag = false;
            } catch (NumberFormatException e1) {
                System.out.println("Некорректный ввод, повторите попытку: ");
                s = in.nextLine();
            }
        }
        return x;
    }

    /**
     * Возвращает long, введенный пользователем
     *
     * @param in Ввод пользователя
     * @return double
     */
    double readDouble(Scanner in) {
        String s = in.nextLine();
        boolean flag = true;
        double y = 0;
        while (s.equals("") || flag || y == Double.POSITIVE_INFINITY || y == Double.NEGATIVE_INFINITY) {
            flag = true;
            try {
                y = Double.parseDouble(s);
                flag = false;
            } catch (NumberFormatException e1) {
                System.out.println("Некорректный ввод, повторите попытку: ");
                s = in.nextLine();
            }
        }
        return y;
    }

    /**
     * Возвращает int, введенный пользователем
     *
     * @param in Ввод пользователя
     * @return int
     */
    int readInt(Scanner in) {
        String s = in.nextLine();
        boolean flag = true;
        int x = 0;
        while (s.equals("") || flag) {
            flag = true;
            try {
                x = Integer.parseInt(s);
                flag = false;
            } catch (NumberFormatException e1) {
                System.out.println("Некорректный ввод, повторите попытку: ");
                s = in.nextLine();
            }
        }
        return x;
    }

    /**
     * Возвращает LocalDate, введенный пользователем
     *
     * @param in Ввод пользователя
     * @return LocalDate
     */
    LocalDate readNullableLocalDate(Scanner in) {
        LocalDate date = LocalDate.now();
        String s = in.nextLine();
        int day;
        int month;
        int year;
        boolean flag = true;
        String[] dat = s.split("\\.");
        if (s.equals("")) {
            date = null;
        } else {
            while (flag) {
                try {
                    day = Integer.parseInt(dat[0]);
                    month = Integer.parseInt(dat[1]);
                    year = Integer.parseInt(dat[2]);
                    date = LocalDate.of(year, month, day);
                    flag = false;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException | DateTimeException f) {
                    System.out.println("Дата введена неверно, повторите попытку");
                    s = in.nextLine();
                    if (s.equals("")) {
                        date = null;
                        break;
                    }
                    dat = s.split("\\.");
                }
            }
        }
        return date;
    }

    /**
     * Возвращает MusicGenre, введенный пользователем
     *
     * @param in Ввод пользователя
     * @return MusicGenre
     */
    MusicGenre readNullableMusicGenre(Scanner in) {
        MusicGenre genre = null;
        String s = in.nextLine();
        boolean flag = true;
        if (s.equals("")) {
            return genre;
        } else {
            while (flag) {
                try {
                    genre = MusicGenre.valueOf(s);
                    flag = false;
                } catch (IllegalArgumentException f) {
                    System.out.println("Жанр введен неверно, повторите попытку");
                    s = in.nextLine();
                    if (s.equals("")) {
                        return genre;
                    }
                }
            }
        }
        return genre;
    }

    /**
     * Возвращает Color, введенный пользователем
     *
     * @param in Ввод пользователя
     * @return Color
     */
    Color readNullableColor(Scanner in) {
        Color color = null;
        String s = in.nextLine();
        boolean flag = true;
        if (s.equals("")) {
            return color;
        } else {
            while (flag) {
                try {
                    color = Color.valueOf(s);
                    flag = false;
                } catch (IllegalArgumentException f) {
                    System.out.println("Цвет введен неверно, повторите попытку");
                    s = in.nextLine();
                    if (s.equals("")) {
                        return color;
                    }
                }
            }
        }
        return color;
    }

    /**
     * Возвращает Country, введенный пользователем
     *
     * @param in Ввод пользователя
     * @return Country
     */
    Country readNullableCountry(Scanner in) {
        Country country = null;
        String s = in.nextLine();
        boolean flag = true;
        if (s.equals("")) {
            return country;
        } else {
            while (flag) {
                try {
                    country = Country.valueOf(s);
                    flag = false;
                } catch (IllegalArgumentException f) {
                    System.out.println("Страна введена неверно, повторите попытку");
                    s = in.nextLine();
                    if (s.equals("")) {
                        return country;
                    }
                }
            }
        }
        return country;
    }

    /**
     * Возвращает объект, поля которых указывает пользователь
     *
     * @param in Scanner
     * @return MusicBand
     */
    public MusicBand readCollectionObject(Scanner in) {
        //name
        System.out.println("Введите имя группы: ");
        String name = this.readString(in);


        //Coordinate x
        System.out.println("Введите координату x: ");
        long x = this.readLong(in);
        while (x > 913) {
            System.out.println("Некорректный ввод, повторите попытку:");
            x = readLong(in);
        }

        //Coordinate y
        System.out.println("Введите координату y: ");
        double y = this.readDouble(in);
        while (y < -224) {
            System.out.println("Некорректный ввод, повторите попытку:");
            y = readDouble(in);
        }

        //NumberOfParticipants
        System.out.println("Введите количество участников группы: ");
        int nop = this.readInt(in);
        while (nop <= 0) {
            System.out.println("Некорректный ввод, повторите попытку:");
            nop = this.readInt(in);
        }

        //establishmentDate
        System.out.println("Введите дату создания(dd.mm.yyyy): ");
        LocalDate date = this.readNullableLocalDate(in);

        //MusicGenre
        System.out.println("Введите жанр(JAZZ, BLUES, MATH_ROCK, K_POP): ");
        MusicGenre genre = this.readNullableMusicGenre(in);

        //frontmans's name
        System.out.println("Введите имя лидера группы: ");
        String frname = this.readString(in);

        //height
        System.out.println("Введите рост лидера группы: ");
        Double height = this.readDouble(in);
        while (height <= 0) {
            System.out.println("Некорректный ввод, повторите попытку:");
            height = this.readDouble(in);
        }

        //eyeColor
        System.out.println("Введите цвет глаз(GREEN, BLUE, ORANGE, WHITE, BLACK, BROWN): ");
        Color eyeColor = this.readNullableColor(in);

        //hairColor
        System.out.println("Введите цвет волос(GREEN, BLUE, ORANGE, WHITE, BLACK, BROWN): ");
        Color hairColor = readNullableColor(in);

        //nationality
        System.out.println("Введите страну(FRANCE, THAILAND, ITALY, SOUTH_KOREA, NORTH_KOREA): ");
        Country nat = readNullableCountry(in);

        return new MusicBand(name, new Coordinates(x, y), nop, date, genre, new Person(frname, height, eyeColor, hairColor, nat));
    }

    public ClassCommand commandFactory(CommandName commandName) {
        ClassCommand classCommand = null;
        switch (commandName) {
            case REMOVE_GREATER:
                classCommand = new RemoveGreaterCommand();
                break;
            case ADD:
                classCommand = new AddCommand();
                break;
            case EXIT:
                classCommand = new ExitCommand();
                break;
            case HELP:
                classCommand = new HelpCommand();
                break;
            case SHOW:
                classCommand = new ShowCommand();
                break;
            case CLEAR:
                classCommand = new ClearCommand();
                break;
            case UPDATE:
                classCommand = new UpdateIdCommand();
                break;
            case HISTORY:
                classCommand = new HistoryCommand();
                break;
            case REMOVE_LOWER:
                classCommand = new RemoveLowerCommand();
                break;
            case EXECUTE_SCRIPT:
                classCommand = new ExecuteScriptCommand();
                break;
            case FILTER_LESS_THEN_NUMBER_OF_PARTICIPANTS:
                classCommand = new FilterLessThanNumberOfParticipantsCommand();
                break;
            case INFO:
                classCommand = new InfoCommand();
                break;
            case MAX_BY_GENRE:
                classCommand = new MaxByGenreCommand();
                break;
            case REMOVE_BY_ID:
                classCommand = new RemoveByIdCommand();
                break;
            case PRINT_DESCENDING:
                classCommand = new PrintDescendingCommand();
                break;
            case ERROR:
                classCommand = new ErrorCommand();
        }
        return classCommand;
    }

    public User readUser(Scanner in) throws NoSuchAlgorithmException {
        System.out.println("Вы уже зарегистрированы? (yes/no)");
        User user = new User();

        String otvet = readString(in);
        while (true) {
            if (otvet.equals("yes")) {
                System.out.println("Введите логин:");
                String login = readString(in);
                System.out.println("Введите пароль:");
                String password = readString(in);
                user.setLogin(login);
                user.setPassword(password);
                user.setStatus(User.Status.UNLOGIN);
                break;
            } else if (otvet.equals("no")) {
                System.out.println("Регистрация нового пользователя");
                System.out.println("Введите логин:");
                String login = readString(in);
                String password;
                while (true) {
                    System.out.println("Введите пароль:");
                    String password1 = readString(in);
                    System.out.println("Введите пароль, еще раз:");
                    String password2 = readString(in);
                    if (password1.equals(password2)) {
                        password = password1;
                        break;
                    }else {
                        System.out.println("Пароли не совпадают, повторите попытку");
                    }
                }
                user.setLogin(login);
                user.setPassword(password);
                user.setStatus(User.Status.UNREGISTER);



                break;
            }else {
                System.out.println("Вы можете ввести только \"yes\" или \"no\". Повторите попытку:");
                otvet = readString(in);
            }
        }
        return user;
    }
}
