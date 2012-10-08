package de.jandankert.ritzelrechner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CalculateActivity extends Activity
{
	private SeekBar anzahlZaehneVorneView;
	private SeekBar anzahlZaehneHintenView;
	private SeekBar trittfrequenzView;

	private TextView geschwindigkeitView;

	private SharedPreferences prefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculate);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		anzahlZaehneVorneView = (SeekBar) findViewById(R.id.seek_anzahl_zaehne_vorne);
		anzahlZaehneHintenView = (SeekBar) findViewById(R.id.seek_anzahl_zaehne_hinten);
		trittfrequenzView = (SeekBar) findViewById(R.id.seek_trittfrequenz);
		geschwindigkeitView = (TextView) findViewById(R.id.geschwindigkeit);

		OnSeekBarChangeListener onSeekBarChangeDoCalculate = new OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				calculate();
			}
		};

		trittfrequenzView
				.setOnSeekBarChangeListener(onSeekBarChangeDoCalculate);
		anzahlZaehneVorneView
				.setOnSeekBarChangeListener(onSeekBarChangeDoCalculate);
		anzahlZaehneHintenView
				.setOnSeekBarChangeListener(onSeekBarChangeDoCalculate);

		calculate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = new MenuInflater(getApplication());
		mi.inflate(R.menu.main, menu);

		return true;
	}

	private void calculate()
	{
		// Umfang im Millimeter
		int umfang = Integer.parseInt(prefs.getString("umfang", "2150"));
		
		int anzahlZaehneVorne = anzahlZaehneVorneView.getProgress()+22;
		((TextView) findViewById(R.id.text_zaehne_vorne)).setText(""
				+ anzahlZaehneVorne);

		int anzahlZaehneHinten = anzahlZaehneHintenView.getProgress()+10;
		((TextView) findViewById(R.id.text_zaehne_hinten)).setText(""
				+ anzahlZaehneHinten);

		int trittfrequenz = trittfrequenzView.getProgress()+40;
		((TextView) findViewById(R.id.text_trittfrequenz)).setText(Integer
				.toString(trittfrequenz));

		BigDecimal geschwindigkeit = new BigDecimal(
				(((double) anzahlZaehneVorne / (double) anzahlZaehneHinten))
						* ((double) (umfang * trittfrequenz)) * 60 / 1000000)
				.setScale(1, RoundingMode.HALF_UP);

		String einheit = "km/h";
		if ("mph".equals(prefs.getString("einheit", "")))
		{
			geschwindigkeit = geschwindigkeit.divide(new BigDecimal("1.61"),RoundingMode.HALF_UP);
			einheit = "mph";
		}

		geschwindigkeitView.setText(geschwindigkeit.toString() + " " + einheit);

	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_preferences:
				startActivity(new Intent(this, PreferencesActivity.class));
				return true;

		}
		return false;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		calculate();
	}

}