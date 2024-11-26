import tkinter as tk
from tkinter import ttk
from startscreen import MainPage
from editfine import EditFinePage
from showfines import ShowFinesPage
from addplayer import AddFinePage, AddPlayerPage
from markfineaspaid import MarkFineAsPaidPage
from sendfines import SendFinesPage
import database


class FineManagementApp(tk.Tk):
    def __init__(self):
        """
        Initializes the FineManagementApp class, which is a Tkinter-based application for managing fines.

        - Sets the window title.
        - Dynamically adjusts the window size based on the user's screen dimensions.
        - Sets a minimum window size of 800x600.
        - Optionally, the window starts in fullscreen mode.
        - Creates a container frame that holds all the pages (frames) of the application.
        - Each page (frame) is created and stored in the `self.frames` dictionary for easy access.
        - Calls `self.show_frame` to display the MainPage as the default page.
        """


        super().__init__()
        self.title("Sakkojen Hallinta")

        # Get the user's screen size and dynamically set the window size
        screen_width = self.winfo_screenwidth()
        screen_height = self.winfo_screenheight()
        self.geometry(f"{screen_width}x{screen_height}") # Set window size to screen dimensions
        self.minsize(800, 600)  # Set a minimum window size for the interface

        # Make the window fullscreen at the start
        self.state('zoomed')

        self.frames = {}
        container = tk.Frame(self)
        container.pack(side="top", fill="both", expand=True)

        # Make the rows and columns flexible to allow for dynamic resizing
        container.grid_rowconfigure(0, weight=1)
        container.grid_columnconfigure(0, weight=1)

        # Loop through each page class and initialize its frame, storing it in the frames dictionary
        for F in (
        MainPage, ShowFinesPage, AddFinePage, AddPlayerPage, EditFinePage,
        MarkFineAsPaidPage, SendFinesPage):
            page_name = F.__name__
            frame = F(parent=container, controller=self)
            self.frames[page_name] = frame

            # Configure the frame to expand and fill available space
            frame.grid(row=0, column=0, sticky="nsew")

        # Display the MainPage frame by default
        self.show_frame("MainPage")

    def show_frame(self, page_name):
        """
        Brings the specified page (frame) to the front and makes it the currently visible frame.

        :param page_name: The name of the frame (page) to be shown.
        :type page_name: str

        - The frame corresponding to `page_name` is brought to the front using the `tkraise()` method.
        - If the page to be displayed is the "MainPage", additional updates are triggered (e.g., updating
          total fines and recent fines).
        """

        # Bring the specified frame (page) to the front of the screen
        frame = self.frames[page_name]
        frame.tkraise()
        if page_name == "MainPage":
            frame.update_total_fines()
            frame.update_recent_fines()


if __name__ == "__main__":
    database.initialize_database()
    app = FineManagementApp()
    app.mainloop()
