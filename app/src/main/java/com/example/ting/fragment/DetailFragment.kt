@file:Suppress("DEPRECATION")

package com.example.ting.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.load
import coil.size.Size
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.example.ting.R
import com.example.ting.adapter.DetailListAdapter
import com.example.ting.databinding.FragmentDetailBinding
import com.example.ting.init.AppInitializer
import com.example.ting.other.setOnItemClickListener
import com.example.ting.viewmodel.TingViewModel
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()
    private val viewModel by activityViewModels<TingViewModel>()
    private val detailListAdapter by lazy { DetailListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.apply {
            adapter = detailListAdapter
            setOnItemClickListener { i, _ ->
                AppInitializer.mXmPlayerManager.playList(detailListAdapter.snapshot().items, i)
            }
        }
        binding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                println(verticalOffset)
                binding.group.isVisible = verticalOffset != -198
                binding.group2.isVisible = verticalOffset == -198
            })
        args.album?.apply {
            binding.appBarImage.load(coverUrl) {
                transformations(BlurTransformation(requireContext(), 25f, 50f))
            }
            binding.image.load(coverUrl) {
                transformations(RoundedCornersTransformation(30f))
            }
            binding.albumTitleTv.text = albumTitle
            binding.image2.load(coverUrl) {
                transformations(RoundedCornersTransformation(30f))
            }
            binding.albumTitleTv2.text = albumTitle
            binding.albumAuthorTv.text = albumIntro
            viewModel.getTrackList(id).observe(viewLifecycleOwner) {
                detailListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class BlurTransformation @JvmOverloads constructor(
    private val context: Context,
    private val radius: Float = DEFAULT_RADIUS,
    private val sampling: Float = DEFAULT_SAMPLING
) : Transformation {

    init {
        require(radius in 0.0..25.0) { "radius must be in [0, 25]." }
        require(sampling > 0) { "sampling must be > 0." }
    }

    override val cacheKey: String = "${BlurTransformation::class.java.name}-$radius-$sampling"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val scaledWidth = (input.width / sampling).toInt()
        val scaledHeight = (input.height / sampling).toInt()
        val output = createBitmap(scaledWidth, scaledHeight, input.config)
        output.applyCanvas {
            scale(1 / sampling, 1 / sampling)
            drawBitmap(input, 0f, 0f, paint)
        }

        var script: RenderScript? = null
        var tmpInt: Allocation? = null
        var tmpOut: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            script = RenderScript.create(context)
            tmpInt = Allocation.createFromBitmap(
                script,
                output,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            tmpOut = Allocation.createTyped(script, tmpInt.type)
            blur = ScriptIntrinsicBlur.create(script, Element.U8_4(script))
            blur.setRadius(radius)
            blur.setInput(tmpInt)
            blur.forEach(tmpOut)
            tmpOut.copyTo(output)
        } finally {
            script?.destroy()
            tmpInt?.destroy()
            tmpOut?.destroy()
            blur?.destroy()
        }

        return output
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is BlurTransformation &&
                context == other.context &&
                radius == other.radius &&
                sampling == other.sampling
    }

    override fun hashCode(): Int {
        var result = context.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + sampling.hashCode()
        return result
    }

    override fun toString(): String {
        return "BlurTransformation(context=$context, radius=$radius, sampling=$sampling)"
    }

    private companion object {
        private const val DEFAULT_RADIUS = 10f
        private const val DEFAULT_SAMPLING = 1f
    }
}