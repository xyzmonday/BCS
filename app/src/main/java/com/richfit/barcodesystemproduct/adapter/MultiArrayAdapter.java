
package com.richfit.barcodesystemproduct.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.richfit.common_lib.widget.AutoMultiLineTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class MultiArrayAdapter<T> extends BaseAdapter implements Filterable, ThemedSpinnerAdapter {

    private final Object mLock = new Object();
    private final LayoutInflater mInflater;
    private List<T> mObjects;
    private int mResource;
    private int mDropDownResource;
    private int mFieldId = 0;
    private boolean mNotifyOnChange = true;
    private Context mContext;
    private ArrayList<T> mOriginalValues;
    private ArrayFilter mFilter;
    private LayoutInflater mDropDownInflater;


    public MultiArrayAdapter(Context context, int resource) {
        this(context, resource, 0, new ArrayList<T>());
    }


    public MultiArrayAdapter(Context context, int resource, int textViewResourceId) {
        this(context, resource, textViewResourceId, new ArrayList<T>());
    }


    public MultiArrayAdapter(Context context, int resource, @NonNull T[] objects) {
        this(context, resource, 0, Arrays.asList(objects));
    }


    public MultiArrayAdapter(Context context, int resource, int textViewResourceId,
                             @NonNull T[] objects) {
        this(context, resource, textViewResourceId, Arrays.asList(objects));
    }


    public MultiArrayAdapter(Context context, int resource, @NonNull List<T> objects) {
        this(context, resource, 0, objects);
    }


    public MultiArrayAdapter(Context context, int resource, int textViewResourceId,
                             @NonNull List<T> objects) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = mDropDownResource = resource;
        mObjects = objects;
        mFieldId = textViewResourceId;
    }

    public void add(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            } else {
                mObjects.add(object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    public void addAll(Collection<? extends T> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
            } else {
                mObjects.addAll(collection);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    public void addAll(T... items) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.addAll(mOriginalValues, items);
            } else {
                Collections.addAll(mObjects, items);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    public void insert(T object, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
            } else {
                mObjects.add(index, object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    public void remove(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            } else {
                mObjects.remove(object);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }


    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    public Context getContext() {
        return mContext;
    }


    public int getCount() {
        return mObjects.size();
    }


    public T getItem(int position) {
        return mObjects.get(position);
    }


    public int getPosition(T item) {
        return mObjects.indexOf(item);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }


    protected void setTextViewMultiLine(TextView tv) {
//        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setSingleLine(false);
        tv.setMinLines(2);
        tv.setMaxLines(5);
//        tv.setTextS   ize(TypedValue.COMPLEX_UNIT_SP,17);
        tv.setTextColor(Color.BLACK);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView,
                                        ViewGroup parent, int resource) {
        View view;
        AutoMultiLineTextView text;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (AutoMultiLineTextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (AutoMultiLineTextView) view.findViewById(mFieldId);
            }
//            if (text != null) {
//                setTextViewMultiLine(text);
//            }
        } catch (ClassCastException e) {
            Log.e("MultiArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "MultiArrayAdapter requires the resource ID to be a TextView", e);
        }

        T item = getItem(position);
        if (item instanceof CharSequence) {
            text.setMultiText((CharSequence) item);
        } else {
            text.setMultiText(item.toString());
        }

        return view;
    }


    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setDropDownViewTheme(Resources.Theme theme) {
        if (theme == null) {
            mDropDownInflater = null;
        } else if (theme == mInflater.getContext().getTheme()) {
            mDropDownInflater = mInflater;
        } else {
            final Context context = new ContextThemeWrapper(mContext, theme);
            mDropDownInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public Resources.Theme getDropDownViewTheme() {
        return mDropDownInflater == null ? null : mDropDownInflater.getContext().getTheme();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mDropDownInflater == null ? mInflater : mDropDownInflater;
        return createViewFromResource(inflater, position, convertView, parent, mDropDownResource);
    }


    public static MultiArrayAdapter<CharSequence> createFromResource(Context context,
                                                                     int textArrayResId, int textViewResId) {
        CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
        return new MultiArrayAdapter<CharSequence>(context, textViewResId, strings);
    }


    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<T>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<T>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<T>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<T> newValues = new ArrayList<T>();

                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);
                    final String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
