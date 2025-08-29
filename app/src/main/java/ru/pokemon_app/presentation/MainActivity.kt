package ru.pokemon_app.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
        setupBottomSheet()
        setupResetButton()
        setupSwipeRefresh()

        viewModel.loadPokemons(1)
    }

    private fun setupRecyclerView() {
        val spanCount = 2
        val layoutManager = GridLayoutManager(this, spanCount)
        binding.pokemonList.layoutManager = layoutManager

        adapter = PokemonAdapter()
        binding.pokemonList.adapter = adapter

        adapter.onItemClick = { pokemon ->
            openPokemonDetails(pokemon)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservers() {
        viewModel.pokemonList.observe(this) { response ->
            val newItems = response?.results ?: emptyList()

            binding.errorText.isVisible = newItems.isEmpty()
            binding.pokemonList.isVisible = newItems.isNotEmpty()

            if (currentPage == 1) {
                adapter.setData(newItems)
            } else {
                adapter.appendData(newItems)
            }

            binding.resetFiltersButton.isVisible = viewModel.isFilterOrSearchActive
            isLoading = false
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.loadingMore.observe(this) { isLoadingMore ->
            adapter.setLoading(isLoadingMore)
            isLoading = isLoadingMore
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                binding.errorText.text = error
                binding.errorText.isVisible = true
                binding.pokemonList.isVisible = false
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

    private fun setupBottomSheet() {
        binding.myBottomSheetButton.setOnClickListener {
            val sheet = SortAndFilterBottomSheet()
            sheet.onApplyFilters =
                { type, minHp, minAttack, minDefense, orderBy ->
                    currentPage = 1
                    viewModel.loadFilteredPokemons(type, minHp, minAttack, minDefense, orderBy)
                }
            sheet.show(supportFragmentManager, "SortFilter")
        }
    }

    private fun setupResetButton() {
        binding.resetFiltersButton.setOnClickListener {
            currentPage = 1
            viewModel.isFilterOrSearchActive = false
            viewModel.loadPokemons(currentPage)
            binding.resetFiltersButton.isVisible = false
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            currentPage = 1
            viewModel.isFilterOrSearchActive = false
            viewModel.loadPokemons(currentPage)
        }
    }

    private fun loadNextPage() {
        if (!isLoading && !viewModel.isFilterOrSearchActive) {
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
        // TODO: переход к деталям
    }

    private fun extractIdFromUrl(url: String): Int {
        return try {
            url.trimEnd('/').split('/').last().toInt()
        } catch (e: Exception) {
            0
        }
    }
}