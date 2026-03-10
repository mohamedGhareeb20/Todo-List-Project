# Todo List

A professional desktop application built in Java, designed to manage daily tasks with an integrated, automated reminder system.

## 🚀 Overview
This project is a task management desktop application that helps users stay organized. Beyond standard CRUD (Create, Read, Update, Delete) functionality, it includes an **automated background monitor** that checks for overdue tasks every minute and provides visual and auditory alerts.

The project follows the **MVC (Model-View-Controller)** design pattern, ensuring clean separation between the data, the business logic, and the user interface.

## 🛠 Features
* **Task Management:** Add, Edit, Delete, and mark tasks as "Done."
* **Automated Reminders:** A background thread (Timer) runs every 60 seconds to monitor deadlines.
* **Smart Status Updates:** Tasks automatically transition to `OVERDUE` status if the deadline passes.
* **Visual/Auditory Alerts:** Overdue tasks are clearly marked on the UI and trigger a system beep to ensure you never miss a deadline.
* **Clean Architecture:** Organized using a professional package structure (`todo` package).

## 🏗 Technical Stack
* **Language:** Java 17+
* **GUI Framework:** AWT (Abstract Window Toolkit)
* **Design Pattern:** MVC (Model-View-Controller)
* **Threading:** EventQueue for thread-safe UI updates and background Timer Tasks.

## 📂 Project Structure
```text
TODO_LIST_PROJECT/
├── src/
│   └── todo/
│       ├── GUI.java             # The View (User Interface)
│       ├── Main.java            # Entry point
│       ├── Manager.java         # The Controller (Logic & Timer)
│       ├── Task.java            # The Model (Data object)
│       └── UpdateListener.java  # Interface for decoupled communication
└── bin/                         # Compiled bytecode (auto-generated)
```

## 💻 How to Run

1. **Clone the repository:**
```bash
   git clone https://github.com/mohamedGhareeb20/Todo-List-Project.git
```

2. **Compile the project:** Navigate to the project root folder in your terminal and run:
```bash
   javac -d bin src/todo/*.java
```

3. **Launch the application:**
```bash
   java -cp bin todo.Main
```
