package com.conx2share.conx2share.ui.contact.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Contact;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactsAdapter extends ArrayAdapter<Contact> implements Filterable {

    private List<Contact> contacts;

    private List<Contact> filteredContacts;

    public ContactsAdapter(Context context, List<Contact> contacts) {

        super(context, R.layout.list_item_contact);
        setContacts(contacts);
        setFilteredContacts(contacts);
    }

    @Override
    public int getCount() {

        return getFilteredContacts().size();
    }

    @Override
    public Contact getItem(int position) {

        return getFilteredContacts().get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View contactView = convertView;

        if (contactView == null) {

            LayoutInflater vi = LayoutInflater.from(getContext());
            contactView = vi.inflate(R.layout.list_item_contact, null);
        }

        final Contact contact = getItem(position);

        if (contact != null) {

            TextView contactNameTextView = (TextView) contactView.findViewById(R.id.contact_name);
            contactNameTextView.setText(contact.getName());

            CheckBox contactCheckBox = (CheckBox) contactView.findViewById(R.id.contact_checkbox);
            contactCheckBox.setChecked(contact.isChosenForInvite());

            TextView contactExtraDetailsTextView = (TextView) contactView.findViewById(R.id.contact_extra_details);
            if (!TextUtils.isEmpty(contact.getEmail())) {
                contactExtraDetailsTextView.setText(contact.getEmail());
            } else {
                contactExtraDetailsTextView.setText("");
            }
        }

        return contactView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = getContacts();
                    results.count = getCount();
                } else {
                    List<Contact> filterResultsData = new ArrayList<>();

                    for (Contact item : getContacts()) {

                        if (getFilterCondition(item, charSequence)) {

                            filterResultsData.add(item);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                setFilteredContacts((List<Contact>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public boolean getFilterCondition(Contact contact, CharSequence sequence) {

        return contact.getName() != null && contact.getName().toLowerCase(Locale.US).contains(sequence);
    }

    public List<Contact> getChosenContacts() {

        List<Contact> chosenContacts = new ArrayList<>();
        for (Contact contact : getContacts()) {

            if (contact.isChosenForInvite()) {

                chosenContacts.add(contact);
            }
        }
        return chosenContacts;
    }

    public List<Contact> getContacts() {

        return contacts;
    }

    public void setContacts(List<Contact> contacts) {

        this.contacts = contacts;
    }

    public List<Contact> getFilteredContacts() {

        return filteredContacts;
    }

    public void setFilteredContacts(List<Contact> filteredContacts) {

        this.filteredContacts = filteredContacts;
    }

}
