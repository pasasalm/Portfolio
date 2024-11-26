import tkinter as tk
from tkinter import ttk, messagebox
import sqlite3
import webbrowser

class SendFinesPage(tk.Frame):
    def __init__(self, parent, controller):
        """
        Initializes the SendFinesPage, setting up the UI elements like buttons, search bar, and player list.
        It also connects to the main controller for navigation.
        """

        super().__init__(parent)
        self.controller = controller

        # Button to navigate back to the MainPage
        ttk.Button(self, text="⬅️",command=lambda: controller.show_frame("MainPage")).pack(anchor="nw")

        # Player search and selection frame
        self.search_frame = ttk.Frame(self)
        self.search_frame.pack(pady=10)

        ttk.Label(self.search_frame, text="Hae pelaajaa pelinumeron tai nimen perusteella:").pack(pady=5)
        self.search_entry = ttk.Entry(self.search_frame, width=30)
        self.search_entry.pack(pady=5)

        ttk.Button(self.search_frame, text="Hae", command=self.search_player).pack(pady=5)
        self.player_list_frame = ttk.Frame(self)
        self.player_list_frame.pack(pady=10, fill="both", expand=True)

        self.player_tree = ttk.Treeview(self.player_list_frame, columns=("PlayerNumber", "PlayerName"), show="headings")
        self.player_tree.heading("PlayerNumber", text="Pelinumero")
        self.player_tree.heading("PlayerName", text="Nimi")
        self.player_tree.pack(side="left", fill="both", expand=True)

        self.scrollbar = ttk.Scrollbar(self.player_list_frame, orient="vertical", command=self.player_tree.yview)
        self.scrollbar.pack(side="right", fill="y")
        self.player_tree.configure(yscrollcommand=self.scrollbar.set)

        # Bind a click event to the player selection
        self.player_tree.bind("<ButtonRelease-1>", self.on_player_select)

        # Load the initial list of players
        self.update_player_list()

    def search_player(self):
        """
        Searches the database for players by their name or number, based on the user input.
        Updates the player list with the search results.
        """

        search_term = self.search_entry.get()
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Query to search for players either by their number or name
        cursor.execute("""
            SELECT pelinumero, pelaajan_nimi 
            FROM Pelaaja 
            WHERE pelinumero LIKE ? OR pelaajan_nimi LIKE ?
        """, (f"%{search_term}%", f"%{search_term}%"))
        players = cursor.fetchall()

        # Clear the current player list and insert the new results
        self.player_tree.delete(*self.player_tree.get_children())

        for player in players:
            pelinumero, nimi = player
            self.player_tree.insert("", "end", values=(pelinumero, nimi))

        conn.close()

    def update_player_list(self):
        """
        Retrieves and displays the full list of players from the database in the player list.
        """

        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Query to get all players ordered by their player number
        cursor.execute("SELECT pelinumero, pelaajan_nimi FROM Pelaaja ORDER BY pelinumero")
        players = cursor.fetchall()

        # Clear the current player list and insert the updated player data
        self.player_tree.delete(*self.player_tree.get_children())
        for player in players:
            pelinumero, nimi = player
            self.player_tree.insert("", "end", values=(pelinumero, nimi))

        conn.close()

    def on_player_select(self, event):
        """
        Handles the selection of a player from the list. Retrieves the player's fines and triggers sending them.
        """

        selected_item = self.player_tree.selection()
        if selected_item:
            pelinumero = self.player_tree.item(selected_item[0])['values'][0]
            self.send_player_fines(pelinumero)

    def send_player_fines(self, pelinumero):
        """
        Retrieves and sends the selected player's unpaid fines via WhatsApp.
        Displays a messagebox with a summary of the fines.
        """

        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Query to get unpaid fines for the selected player
        cursor.execute("""
            SELECT Pelaaja.pelaajan_nimi, Pelaaja.puhelin_numero, Sakko.sakon_syy, Sakko.sakon_maara, Sakko.pvm 
            FROM Pelaaja 
            JOIN Sakko ON Pelaaja.pelinumero = Sakko.pelinumero 
            WHERE Pelaaja.pelinumero = ? AND Sakko.maksettu = 0
        """, (pelinumero,))
        fines_data = cursor.fetchall()

        # Prepare fine details for the WhatsApp message
        if fines_data:
            name, phone, _, _, _ = fines_data[0]
            fines_summary = [f"{reason} - {amount} € - {date}" for _, _, reason, amount, date in fines_data]
            total_amount = sum([amount for _, _, _, amount, _ in fines_data])

            fines_list = "\n".join(fines_summary)
            message = (
                f"Hei {name},\n"
                f"Sinulla on seuraavat sakot:\n"
                f"{fines_list}\n"
                f"Yhteensä: {total_amount} €\n"
            )

            # Send the message via WhatsApp and show confirmation
            self.send_whatsapp_message(phone, message)
            messagebox.showinfo("Sakot lähetetty", f"Sakot lähetetty pelaajalle {name} WhatsAppissa.")

        else:
            # Show info if no unpaid fines are found
            messagebox.showinfo("Ei maksamattomia sakkoja", "Tällä pelaajalla ei ole maksamattomia sakkoja.")

        conn.close()

    def send_whatsapp_message(self, phone, message):
        """
        Sends the fine details to the player's phone number via WhatsApp.
        Opens the WhatsApp web link with the pre-filled message.
        """

        formatted_message = message.replace(" ", "%20").replace("\n", "%0A")
        url = f"https://api.whatsapp.com/send?phone={phone}&text={formatted_message}"
        webbrowser.open(url)