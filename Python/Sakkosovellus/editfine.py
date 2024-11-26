import tkinter as tk
from tkinter import ttk, messagebox
import sqlite3
from datetime import datetime
from startscreen import MainPage

class EditFinePage(tk.Frame):
    def __init__(self, parent, controller):
        """
        Initializes the EditFinePage, sets up UI elements for editing and deleting fines,
        and displays the list of fines.

        Parameters:
            parent (tkinter.Frame): The parent frame.
            controller (object): The controller object.
        """
        super().__init__(parent)
        self.controller = controller

        # Back button to navigate to the MainPage
        ttk.Button(self, text="⬅️", command=lambda: controller.show_frame("MainPage")).pack(anchor="nw")

        # Main content frame
        self.content_frame = ttk.Frame(self)
        self.content_frame.pack(fill="both", expand=True)

        # Treeview to display the list of fines
        self.fine_tree = ttk.Treeview(self.content_frame, columns=("ID", "Player", "Reason", "Amount", "Date"), show="headings")
        self.fine_tree.heading("ID", text="ID")
        self.fine_tree.heading("Player", text="Pelaaja")
        self.fine_tree.heading("Reason", text="Syy")
        self.fine_tree.heading("Amount", text="Määrä")
        self.fine_tree.heading("Date", text="Pvm")
        self.fine_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar for the fines Treeview
        self.scrollbar = ttk.Scrollbar(self.content_frame, orient="vertical", command=self.fine_tree.yview)
        self.scrollbar.pack(side="right", fill="y")
        self.fine_tree.configure(yscrollcommand=self.scrollbar.set)

        # Frame for editing fine details
        self.edit_frame = ttk.Frame(self)
        self.edit_frame.pack(fill="both", expand=True)

        # Entry fields for fine details
        ttk.Label(self.edit_frame, text="Sakon ID:").pack(pady=5)
        self.fine_id_entry = ttk.Entry(self.edit_frame)
        self.fine_id_entry.pack(pady=5)

        ttk.Label(self.edit_frame, text="Sakon Syy:").pack(pady=5)
        self.syy_entry = ttk.Entry(self.edit_frame)
        self.syy_entry.pack(pady=5)

        ttk.Label(self.edit_frame, text="Sakon Määrä:").pack(pady=5)
        self.maara_entry = ttk.Entry(self.edit_frame)
        self.maara_entry.pack(pady=5)

        ttk.Label(self.edit_frame, text="Pvm:").pack(pady=5)
        self.pvm_entry = ttk.Entry(self.edit_frame)
        self.pvm_entry.pack(pady=5)

        # Buttons to update and delete the fine
        ttk.Button(self.edit_frame, text="Päivitä Sakko", command=self.update_fine).pack(pady=5)
        ttk.Button(self.edit_frame, text="Poista Sakko", command=self.delete_fine).pack(pady=5)

        # Load fines on initialization
        self.load_fines()

        # Bind selection event to the on_item_select method
        self.fine_tree.bind("<ButtonRelease-1>", self.on_item_select)

    def load_fines(self):
        """
        Fetches and displays the list of all fines from the database.

        Returns:
            None
        """
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch fines from the database
        cursor.execute("""
            SELECT Sakko.id, Pelaaja.pelaajan_nimi, Sakko.sakon_syy, Sakko.sakon_maara, Sakko.pvm
            FROM Sakko JOIN Pelaaja ON Sakko.pelinumero = Pelaaja.pelinumero ORDER BY Sakko.id DESC
        """)
        fines = cursor.fetchall()

        # Clear previous fines
        for item in self.fine_tree.get_children():
            self.fine_tree.delete(item)

        # Insert fines into the Treeview
        for fine in fines:
            self.fine_tree.insert("", "end", values=fine)

        conn.close()

    def on_item_select(self, event):
        """
        Handles the event when a fine is selected from the list.
        Populates the entry fields with the selected fine's details.

        Parameters:
            event (tkinter.Event): The event object.

        Returns:
            None
        """
        selected_item = self.fine_tree.selection()
        if selected_item:
            item_values = self.fine_tree.item(selected_item, "values")
            if item_values:
                self.fine_id_entry.delete(0, tk.END)
                self.fine_id_entry.insert(0, item_values[0])

                self.syy_entry.delete(0, tk.END)
                self.syy_entry.insert(0, item_values[2])

                self.maara_entry.delete(0, tk.END)
                self.maara_entry.insert(0, item_values[3])

                self.pvm_entry.delete(0, tk.END)
                self.pvm_entry.insert(0, item_values[4])

    def update_fine(self):
        """
        Updates the fine details in the database based on the entry fields.

        Returns:
            None
        """
        fine_id = self.fine_id_entry.get()
        syy = self.syy_entry.get()
        maara = self.maara_entry.get()
        pvm = self.pvm_entry.get()

        if not fine_id or not syy or not maara or not pvm:
            messagebox.showerror("Virhe", "Kaikki kentät on täytettävä.")
            return

        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Update the fine details in the database
        cursor.execute(
            "UPDATE Sakko SET sakon_syy =?, sakon_maara =?, pvm =? WHERE id =?",
            (syy, maara, pvm, fine_id))

        conn.commit()
        conn.close()

        # Reload fines and update totals on the MainPage
        self.load_fines()
        self.controller.frames["MainPage"].update_total_fines()
        self.controller.frames["MainPage"].update_recent_fines()

        messagebox.showinfo("Onnistui", "Sakko päivitetty onnistuneesti.")

    def delete_fine(self):
        """
        Deletes the fine from the database based on the fine ID in the entry field.

        Returns:
            None
        """
        fine_id = self.fine_id_entry.get()

        if not fine_id:
            messagebox.showerror("Virhe", "Sakko ID on annettava.")
            return

        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch the fine details before deletion
        cursor.execute("SELECT pelinumero, sakon_maara FROM Sakko WHERE id =?", (fine_id,))
        fine = cursor.fetchone()

        if fine is None:
            messagebox.showerror("Virhe", "Sakkoa ei löytynyt.")
            conn.close()
            return

        pelinumero, sakon_maara = fine  # Now these variables are defined only if fine is found

        # Delete the fine from the database
        cursor.execute("DELETE FROM Sakko WHERE id =?", (fine_id,))

        # Update the player's unpaid fines
        cursor.execute(
            "UPDATE Pelaaja SET sakkoja_maksamatta = sakkoja_maksamatta -? WHERE pelinumero =?",
            (sakon_maara, pelinumero))

        conn.commit()
        conn.close()

        # Reload fines and update totals on the MainPage
        self.load_fines()
        self.controller.frames["MainPage"].update_total_fines()
        self.controller.frames["MainPage"].update_recent_fines()

        messagebox.showinfo("Onnistui", "Sakko poistettu onnistuneesti.")
