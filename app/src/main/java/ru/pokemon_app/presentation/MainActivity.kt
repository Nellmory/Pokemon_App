package ru.pokemon_app.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.pokemon_app.databinding.ActivityMainBinding
import ru.pokemon_app.domain.model.PokemonListItem

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PokemonAdapter
    private val viewModel: PokemonViewModel by viewModels()
    private var isLoading = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupScrollListener()
        setupSearch()
        setupFab()

        // Загрузка данных
        viewModel.loadPokemons(1)
    }

    private fun setupRecyclerView() {
        val spanCount = 2
        val layoutManager = GridLayoutManager(this, spanCount)
        binding.pokemonList.layoutManager = layoutManager

        // Передаем ViewModel в адаптер
        adapter = PokemonAdapter(viewModel)
        binding.pokemonList.adapter = adapter

        // Обработка клика по элементу
        adapter.onItemClick = { pokemon ->
            openPokemonDetails(pokemon)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservers() {
        viewModel.pokemonList.observe(this) { response ->
            val newItems = response?.results ?: emptyList()
            if (currentPage == 1) {
                adapter.setData(newItems)
            } else {
                adapter.appendData(newItems)
            }
            isLoading = false
        }

        viewModel.loading.observe(this) { isLoading ->
            this.isLoading = isLoading
            // Можно показать/скрыть прогресс в адаптере
            adapter.setLoading(isLoading)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                // Показать ошибку
                android.widget.Toast.makeText(this, error, android.widget.Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }
    }

    private fun setupScrollListener() {
        binding.pokemonList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Загружаем следующую страницу когда прокрутили до конца
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                    loadNextPage()
                }
            }
        })
    }

    private fun setupSearch() {
        binding.searchBar.setOnClickListener {
            binding.searchView.show()
        }

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            if (query.isNotEmpty()) {
                searchPokemons(query)
                binding.searchView.hide()
            }
            true
        }
    }

    private fun setupFab() {
        binding.fabScrollToTop.setOnClickListener {
            binding.pokemonList.smoothScrollToPosition(0)
        }
    }

    private fun loadNextPage() {
        if (!isLoading) {
            currentPage++
            viewModel.loadPokemons(currentPage)
        }
    }

    private fun searchPokemons(query: String) {
        currentPage = 1
        viewModel.loadPokemons(1, query)
    }

    private fun openPokemonDetails(pokemonItem: PokemonListItem) {
        val pokemonId = extractIdFromUrl(pokemonItem.url)
        // Реализация перехода к деталям покемона
        /*val intent = Intent(this, PokemonDetailActivity::class.java).apply {
            putExtra("POKEMON_ID", pokemonId)
        }
        startActivity(intent)*/
    }

    private fun extractIdFromUrl(url: String): Int {
        return try {
            url.trimEnd('/').split('/').last().toInt()
        } catch (e: Exception) {
            0
        }
    }
}